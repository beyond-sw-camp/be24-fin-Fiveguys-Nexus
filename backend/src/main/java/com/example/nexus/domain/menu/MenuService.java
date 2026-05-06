package com.example.nexus.domain.menu;

import com.example.nexus.common.exception.BaseException;
import com.example.nexus.domain.menu.model.Menu;
import com.example.nexus.domain.menu.model.MenuCategory;
import com.example.nexus.domain.menu.model.MenuDto;
import com.example.nexus.domain.product.ProductRepository;
import com.example.nexus.domain.product.model.Product;
import jakarta.persistence.EntityNotFoundException;
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

    public List<MenuDto.MenuCategoryRes> menucategory() {
        List<MenuCategory> res = menuCategoryRepository.findAll();
        List<MenuDto.MenuCategoryRes> result = new ArrayList<>();

        for (MenuCategory data: res){
            result.add(MenuDto.MenuCategoryRes.from(data));
        }
        return result;
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


        // 3. DTO의 toEntity 호출 (조회한 객체들을 전달)
        Menu menu = dto.toEntity(category, products);

        // 4. 저장
        menuRepository.save(menu);
    }
}
