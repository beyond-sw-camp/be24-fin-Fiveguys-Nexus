package com.example.nexus.domain.menu;

import com.example.nexus.common.exception.BaseException;
import com.example.nexus.domain.menu.model.Menu;
import com.example.nexus.domain.menu.model.MenuCategory;
import com.example.nexus.domain.menu.model.MenuDto;
import com.example.nexus.domain.menu.model.MenuItem;
import com.example.nexus.domain.product.ProductRepository;
import com.example.nexus.domain.product.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.nexus.common.model.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final ProductRepository productRepository;

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.menu-bucket}")
    private String menuBucket;

    @Transactional(readOnly = true)
    public MenuDto.MenuPageRes list(MenuDto.MenuSearchPagingReq searchReq, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Menu> result = menuRepository.findMenusBySearch(searchReq.getKeyword(), searchReq.getCategoryIdx(), pageRequest);

        return MenuDto.MenuPageRes.from(result);
    }

    public MenuDto.MenuItemListRes menuItemList(Long menuIdx) {
        Optional<Menu> res = menuRepository.findById(menuIdx);

        if(res.isPresent()){
            Menu menu = res.get();
            return MenuDto.MenuItemListRes.from(menu);
        }
        return null;
    }

    public Map<String, String> getPresignedUrl(String fileName) {
        // 1. 경로 및 고유 파일명 생성 (기존 로직 유지)
        String path = createPath(fileName);

        // 2. S3 업로드 요청 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(menuBucket)
                .key(path)
                .build();

        // 3. Pre-signed 요청 설정 (유효시간 분)
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(putObjectRequest)
                .build();

        // 4. URL 생성 및 반환
        String url = s3Presigner.presignPutObject(presignRequest).url().toString();

        return Map.of(
                "url", url,
                "fileName", path // 프론트에서 나중에 DB에 저장할 때 필요하니까 같이 보내주는 게 좋아요!
        );
    }
    private String createPath(String fileName) {
        return UUID.randomUUID().toString() + "-nexus-" + fileName;
    }

    @Transactional
    public void menuRegister(MenuDto.MenuRegReq dto) {
        String newFilePath = dto.getImgPath();

        try {
            // 메뉴명 존재 확인
            if (menuRepository.existsByMenuName(dto.getMenuName())) {
                throw new BaseException(DUPLICATE_MENU_NAME);
            }

            // 카테고리 존재 여부 확인
            MenuCategory category = menuCategoryRepository.findById(dto.getMenuCategoryIdx())
                    .orElseThrow(() -> new BaseException(NOT_FOUND_CATEGORY));


            List<Long> productIds = Collections.emptyList();
            List<Product> products = Collections.emptyList();

            if (dto.getMenuItemList() != null && !dto.getMenuItemList().isEmpty()) {
                productIds = dto.getMenuItemList().stream()
                        .map(MenuDto.MenuItemReq::getProductIdx).toList();
                products = productRepository.findAllById(productIds);
            }


            Menu menu = dto.toEntity(category, products);
            menuRepository.save(menu);

        } catch (Exception e) {
            // 등록 중 에러가 나면 DB는 롤백되지만, 이미 업로드된 파일은 좀비가 되므로 삭제 처리
            if (newFilePath != null && !newFilePath.isEmpty()) {
                try {
                    deleteS3Object(newFilePath);
                } catch (Exception ignored) {
                    // 롤백 중 S3 삭제 실패는 무시하여 원래 예외를 보존합니다.
                }
            }

            // 예외 다시 던지기
            if (e instanceof BaseException) throw (BaseException) e;
            throw new RuntimeException(e);
        }

    }

    @Transactional
    public void menuUpdate(Long menuIdx, MenuDto.MenuRegReq dto) {
        String newFilePath = dto.getImgPath();
        String oldFilePath = "";

        try{
            Menu menu = menuRepository.findByIdxAndIsDeletedFalse(menuIdx)
                    .orElseThrow(() -> new BaseException(NOT_FOUND_MENU));

            // 카테고리 존재 여부 확인
            MenuCategory category = menuCategoryRepository.findById(dto.getMenuCategoryIdx())
                    .orElseThrow(() -> new BaseException(NOT_FOUND_CATEGORY));

            oldFilePath = menu.getImgPath();

            // 메뉴명 중복 확인
            if (menuRepository.existsByMenuNameAndIdxNot(dto.getMenuName(), menuIdx)) {
                throw new BaseException(DUPLICATE_MENU_NAME);
            }

            // 제품 조회
            List<Long> productIds = Optional.ofNullable(dto.getMenuItemList()).orElseGet(Collections::emptyList).stream()
                    .map(MenuDto.MenuItemReq::getProductIdx).toList();
            List<Product> products = productRepository.findAllById(productIds);

            // 새 파일이 들어왔고, 기존 파일과 다를 경우에만 동기화 등록
            if (newFilePath != null && !newFilePath.equals(oldFilePath) && oldFilePath != null && !oldFilePath.isBlank()) {
                String finalOldFile = oldFilePath;
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        deleteS3Object(finalOldFile);
                    }
                });
            }

            menu.update(dto.getMenuName(), dto.getPrice(), dto.getImgPath(), category);

            // 해당 메뉴에 재료 리스트(MenuItem) 비우기
            menu.getMenuItemList().clear();

            Map<Long, Product> productMap = products.stream()
                    .collect(Collectors.toMap(Product::getIdx, p -> p));

            for (MenuDto.MenuItemReq itemReq : Optional.ofNullable(dto.getMenuItemList()).orElseGet(Collections::emptyList)) {
                Product product = productMap.get(itemReq.getProductIdx());

                // 상품 찾아보기
                if (product == null) throw new BaseException(NOT_FOUND_PRODUCT);

                MenuItem newItem = itemReq.toEntity(menu, product);
                menu.getMenuItemList().add(newItem);
            }

        }catch (Exception e) {
            if (newFilePath != null && !newFilePath.isEmpty() && !newFilePath.equals(oldFilePath)) {
                try {
                    deleteS3Object(newFilePath);
                } catch (Exception ignored) {
                    // 롤백 중 S3 삭제 실패는 무시하여 원래 예외를 보존합니다.
                }
            }

            if (e instanceof BaseException) throw (BaseException) e;
            throw new RuntimeException(e);
        }
    }

    private void deleteS3Object(String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(menuBucket)
                    .key(key)
                    .build());
        } catch (Exception e) {
            // S3 삭제 실패 시 로그를 남기거나 무시하여 원래 예외가 전파되도록 합니다.
        }
    }

    @Transactional
    public void menuDelete(Long menuIdx) {
        // 메뉴 삭제 여부 확인
        Menu menu = menuRepository.findByIdxAndIsDeletedFalse(menuIdx)
                .orElseThrow(() -> new BaseException(NOT_FOUND_MENU));

        menu.deleteTrue();
    }

    @Transactional(readOnly = true)
    public List<MenuDto.MenuCategoryRes> menucategory() {
        List<MenuCategory> res = menuCategoryRepository.findAll();
        List<MenuDto.MenuCategoryRes> result = new ArrayList<>();

        for (MenuCategory data: res){
            result.add(MenuDto.MenuCategoryRes.from(data));
        }
        return result;
    }

    @Transactional
    public void menucategoryReg(MenuDto.MenuCategoryRegReq dto) {
        Optional<MenuCategory> existing = menuCategoryRepository.findByMenuCategoryName(dto.getMenuCategoryName());

        if (existing.isPresent()) {
            MenuCategory category = existing.get();
            // 존재 여부 확인
            if (!category.isDeleted()) {
                throw new BaseException(DUPLICATE_CATEGORY_NAME);
            }
            category.remenu();
        } else {
            menuCategoryRepository.save(MenuCategory.builder()
                    .menuCategoryName(dto.getMenuCategoryName())
                    .build());
        }
    }

    @Transactional
    public void menuCategoryDelete(Long categoryIdx) {
        // 카테고리 존재 여부
        MenuCategory category = menuCategoryRepository.findById(categoryIdx)
                .orElseThrow(() -> new BaseException(NOT_FOUND_CATEGORY));

        // 메뉴에서 현재 카테고리 사용 여부 확인
        if (menuRepository.existsByMenuCategoryIdxAndIsDeletedFalse(categoryIdx)) {
            throw new BaseException(CATEGORY_IN_USE);
        }

        category.deleteTrue();
    }
}
