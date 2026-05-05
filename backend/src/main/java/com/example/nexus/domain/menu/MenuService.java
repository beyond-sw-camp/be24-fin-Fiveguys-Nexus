package com.example.nexus.domain.menu;

import com.example.nexus.domain.menu.model.Menu;
import com.example.nexus.domain.menu.model.MenuCategory;
import com.example.nexus.domain.menu.model.MenuDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;
    private final MenuCategoryRepository menuCategoryRepository;

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
}
