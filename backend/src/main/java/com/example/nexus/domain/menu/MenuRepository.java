package com.example.nexus.domain.menu;

import com.example.nexus.domain.menu.model.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    // 가맹점명 중복 여부
    boolean existsByMenuName(String menuName);

    // 메뉴 이름 중복 확인
    boolean existsByMenuNameAndIdxNot(String menuName, Long idx);

    // 메뉴 삭제 여부 확인
    Optional<Menu> findByIdxAndIsDeletedFalse(Long menuIdx);

    Page<Menu> findAllByIsDeletedFalse(Pageable pageable);
}
