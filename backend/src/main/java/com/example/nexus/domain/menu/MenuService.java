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

    @Value("${spring.cloud.aws.s3.menu-bucket}")
    private String menuBucket;

    @Transactional(readOnly = true)
    public MenuDto.MenuPageRes list(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Menu> result = menuRepository.findAll(pageRequest);

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
    }

    @Transactional
    public void menuUpdate(Long menuIdx, MenuDto.MenuRegReq dto) {

        // 메뉴명 중복 확인
        if (menuRepository.existsByMenuName(dto.getMenuName())) {
            throw new BaseException(DUPLICATE_MENU_NAME);
        }

        // 메뉴 존재 여부 확인
        Menu menu = menuRepository.findById(menuIdx)
                .orElseThrow(() -> new BaseException(NOT_FOUND_MENU));

        // 카테고리 존재 여부 확인
        MenuCategory category = menuCategoryRepository.findById(dto.getMenuCategoryIdx())
                .orElseThrow(() -> new BaseException(NOT_FOUND_CATEGORY));

        // 제품 조회
        List<Long> productIds = dto.getMenuItemList().stream()
                .map(MenuDto.MenuItemReq::getProductIdx).toList();
        List<Product> products = productRepository.findAllById(productIds);

        // 메뉴 업데이트
        menu.update(dto.getMenuName(), dto.getPrice(), dto.getImgPath(), category);

        // 해당 메뉴에 재료 리스트(MenuItem) 비우기
        menu.getMenuItemList().clear();

        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getIdx, p -> p));

        for (MenuDto.MenuItemReq itemReq : dto.getMenuItemList()) {
            Product product = productMap.get(itemReq.getProductIdx());
            if (product == null) throw new BaseException(NOT_FOUND_PRODUCT);

            // 기존에 잘 만들어둔 toEntity를 그대로 활용합니다!
            MenuItem newItem = itemReq.toEntity(menu, product);
            menu.getMenuItemList().add(newItem);
        }
    }
}
