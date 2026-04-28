package com.example.nexus.domain.news;

import com.example.nexus.domain.news.model.NewsSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsSummaryRepository extends JpaRepository<NewsSummary, Long> {
}
