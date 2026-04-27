package com.example.nexus.storenotification.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "store_notification")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_notification_idx", nullable = false)
    private Long idx;

    private Type type;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    private LocalDateTime createdAt;

    @Column(name = "store_idx", nullable = false)
    private Long storeIdx;


    private String userName;
    @Column(nullable = false)
    private String tell;
}
