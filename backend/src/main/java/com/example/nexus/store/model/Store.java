package com.example.nexus.store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "store")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_idx", nullable = false, unique = true)
    private Long idx;  // 가맹점 번호

    @Column(name = "store_name", nullable = false, unique = true)
    private String storeName; // 가맹점명

    @Column(nullable = false, unique = true)
    private String address;  // 가맹점 위치

    @Column(name = "file_path", nullable = false)
    private String filePath;  // 사업장 등록증 경로

    @Column(nullable = false,  unique = true)
    private String business; // 사업자 번호

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;  // 개업

    @Column(name = "closed_at")
    private LocalDateTime closedAt;  // 폐업

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;  // 삭제 여부

}
