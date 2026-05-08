package com.example.nexus.domain.store;

import com.example.nexus.domain.store.model.Store;
import com.example.nexus.domain.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store,Long> {
    Optional<Store> findByUserIdx(Long userIdx);

    List<Store> findByStoreNameContainingIgnoreCase(String keyword);

    Long user(User user);

    Optional<Store> findByStoreName(String storeName);

    Optional<Store> findByBusiness(String business);

    // 전체 조회 시 이름 또는 주소 검색
    @Query("SELECT s FROM Store s WHERE " +
            "(s.storeName LIKE %:keyword% OR s.address LIKE %:keyword%) " +
            "ORDER BY s.isDeleted ASC, s.createdAt DESC")
    Page<Store> findByKeywordAll(@Param("keyword")String keyword, Pageable pageable);

    // 특정 상태(isDeleted)이면서 이름 또는 주소 검색
    @Query("SELECT s FROM Store s WHERE s.isDeleted = :isDeleted AND " +
            "(s.storeName LIKE %:keyword% OR s.address LIKE %:keyword%) " +
            "ORDER BY s.createdAt DESC")
    Page<Store> findByStatusAndKeyword(@Param("isDeleted")boolean isDeleted, @Param("keyword") String keyword, Pageable pageable);

    // ALL
    @Query("SELECT s FROM Store s ORDER BY s.isDeleted ASC, s.createdAt DESC")
    Page<Store> findAllCustom(Pageable pageable);

    // 검색 없이 '입점'만 조회할 때: 최신 등록순 정렬[cite: 7]
    Page<Store> findByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable); // 입점(false)
    // 검색 없이 '폐점'만 조회할 때: 최신 폐업일순 또는 등록순 정렬[cite: 7]
    Page<Store> findByIsDeletedTrueOrderByClosedAtDesc(Pageable pageable);

    // 본사 대시보드용 - 전체 매장 수 KPI 카드
    long countByIsDeletedFalse();

    // 본사 대시보드용 - 신규 매장 수 (기준일 이후 생성)
    long countByIsDeletedFalseAndCreatedAtAfter(LocalDateTime since);

    // 본사 대시보드용 - 이전 기간 매장 수 (증감률 계산)
    long countByIsDeletedFalseAndCreatedAtBefore(LocalDateTime until);


    // 가맹점 폐점수 조회
    long countByIsDeletedTrue();
}
