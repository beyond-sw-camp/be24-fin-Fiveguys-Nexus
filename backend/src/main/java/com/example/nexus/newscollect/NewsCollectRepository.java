package com.example.nexus.newscollect;

import com.example.nexus.newscollect.model.NewsCollect;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsCollectRepository extends JpaRepository<NewsCollect, Long> {
}
