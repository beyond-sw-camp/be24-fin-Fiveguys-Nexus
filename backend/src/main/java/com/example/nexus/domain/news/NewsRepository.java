package com.example.nexus.domain.news;

import com.example.nexus.domain.news.model.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long> {
}
