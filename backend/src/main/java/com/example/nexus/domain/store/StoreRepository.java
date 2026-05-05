package com.example.nexus.domain.store;

import com.example.nexus.domain.store.model.Store;
import com.example.nexus.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store,Long> {
    Optional<Store> findByUserIdx(Long userIdx);

    List<Store> findByStoreNameContainingIgnoreCase(String keyword);

    Long user(User user);

    Optional<Store> findByStoreName(String storeName);

    Optional<Store> findByBusiness(String business);

    // 대시보드용 - 본사 대시보드 전체 매장 수 KPI 카드
    long countByIsDeletedFalse();

    // 대시보드용 - 본사 대시보드 신규 매장 수 (기준일 이후 생성)
    long countByIsDeletedFalseAndCreatedAtAfter(LocalDateTime since);

    // 대시보드용 - 본사 대시보드 이전 기간 매장 수 (증감률 계산)
    long countByIsDeletedFalseAndCreatedAtBefore(LocalDateTime until);
}
