package com.example.nexus.headnotification.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Head_Notification")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HeadNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "head_notification", nullable = false)
    private Long idx;
    @Column(nullable = false)
    private Enum type;
    @Column(name = "is_read", nullable = false)
    private boolean isRead;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
