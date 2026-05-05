package com.example.nexus.domain.store;

import com.example.nexus.domain.store.model.Store;
import com.example.nexus.domain.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    // 전체 조회 시 이름 또는 주소 검색
    @Query("SELECT s FROM Store s WHERE " +
            "(s.storeName LIKE %:keyword% OR s.address LIKE %:keyword%)")
    Page<Store> findByKeywordAll(String keyword, Pageable pageable);

    // 특정 상태(isDeleted)이면서 이름 또는 주소 검색
    @Query("SELECT s FROM Store s WHERE s.isDeleted = :isDeleted AND (s.storeName LIKE %:keyword% OR s.address LIKE %:keyword%)")
    Page<Store> findByStatusAndKeyword(boolean isDeleted, String keyword, Pageable pageable);

    // 검색 없음/ 가맹점 페이지 처리 입점, 폐점 데이터 조회
    Page<Store> findByIsDeletedFalse(Pageable pageable); // 입점(false)
    Page<Store> findByIsDeletedTrue(Pageable pageable);  // 폐점(true)
}
