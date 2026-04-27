package com.example.nexus.newssummary;

import com.example.nexus.newssummary.model.NewsSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsSummaryRepository extends JpaRepository<NewsSummary, Long> {
}
