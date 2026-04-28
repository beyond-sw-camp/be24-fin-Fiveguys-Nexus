package com.example.nexus.domain.store.model;

import com.example.nexus.domain.menu.model.Menu;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "store")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_idx")
    private Long idx;

    @Column(name = "store_name", nullable = false, unique = true)
    private String storeName;

    @Column(nullable = false)
    private String address;

    @Column(name = "address_detail", nullable = false)
    private String addressDetail;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(nullable = false,  unique = true)
    private String business;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    List<Menu> menuList;
}
