package com.example.nexus.domain.news;

import com.example.nexus.domain.news.model.NewsCollect;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsCollectRepository extends JpaRepository<NewsCollect, Long> {
}
