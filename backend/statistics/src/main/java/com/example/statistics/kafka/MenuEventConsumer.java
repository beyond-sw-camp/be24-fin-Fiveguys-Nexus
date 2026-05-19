package com.example.statistics.kafka;

import com.example.statistics.domain.menu.MenuCategoryRepository;
import com.example.statistics.domain.menu.MenuRepository;
import com.example.statistics.domain.menu.model.Menu;
import com.example.statistics.domain.menu.model.MenuCategory;
import com.example.statistics.event.KafkaTopics;
import com.example.statistics.event.MenuEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MenuEventConsumer {

    private final MenuRepository menuRepository;
    private final MenuCategoryRepository menuCategoryRepository;

    @KafkaListener(topics = KafkaTopics.MENU_CREATED)
    @Transactional
    public void onMenuCreated(MenuEvent event) {
        log.info("[menu.created] received: idx={}, name={}, category={}",
                event.menuIdx(), event.menuName(), event.menuCategoryName());
        upsert(event);
    }

    @KafkaListener(topics = KafkaTopics.MENU_UPDATED)
    @Transactional
    public void onMenuUpdated(MenuEvent event) {
        log.info("[menu.updated] received: idx={}, name={}", event.menuIdx(), event.menuName());
        upsert(event);
    }

    @KafkaListener(topics = KafkaTopics.MENU_DELETED)
    @Transactional
    public void onMenuDeleted(MenuEvent event) {
        log.info("[menu.deleted] received: idx={}", event.menuIdx());
        upsert(event);   // isDeleted=true 페이로드라 자연스레 soft delete
    }

    private void upsert(MenuEvent event) {
        // 1) 카테고리 먼저 upsert (Menu의 FK 무결성 보장)
        MenuCategory category = null;
        if (event.menuCategoryIdx() != null) {
            category = MenuCategory.builder()
                    .idx(event.menuCategoryIdx())
                    .menuCategoryName(event.menuCategoryName())
                    .isDeleted(false)
                    .build();
            menuCategoryRepository.save(category);
        }

        // 2) 메뉴 upsert
        Menu menu = Menu.builder()
                .idx(event.menuIdx())
                .menuName(event.menuName())
                .price(event.price())
                .isDeleted(Boolean.TRUE.equals(event.isDeleted()))
                .menuCategory(category)
                .build();
        menuRepository.save(menu);
    }
}