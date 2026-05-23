package com.example.statistics.domain.store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLInsert;

@Entity
@Table(name = "store")
@SQLInsert(sql =
        "INSERT INTO store (store_idx, store_name, is_deleted) " +
        "VALUES (?, ?, ?) " +
        "ON DUPLICATE KEY UPDATE " +
        "store_name = VALUES(store_name), " +
        "is_deleted = VALUES(is_deleted)")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {

    @Id
    @Column(name = "store_idx")
    private Long idx;                            // ← @GeneratedValue 제거 (이벤트에서 받은 ID 그대로 사용)

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}