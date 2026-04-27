package com.example.nexus.newscollect.model;

import com.example.nexus.common.enums.NewsCollectStatus;
import com.example.nexus.common.enums.NewsCollectTarget;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "news_collect")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewsCollect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_collect_idx")
    private Long idx;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "url", nullable = false, length = 1000)
    private String url;

    @Column(name = "snippet", nullable = false, columnDefinition = "TEXT")
    private String snippet;

    @Column(name = "published_at", nullable = false)
    private LocalDateTime publishedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "target", nullable = false)
    private NewsCollectTarget target;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NewsCollectStatus status;

    @Column(name = "collected_at", nullable = false)
    private LocalDateTime collectedAt;
}
