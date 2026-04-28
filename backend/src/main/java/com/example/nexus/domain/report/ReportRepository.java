package com.example.nexus.domain.report;

import com.example.nexus.domain.report.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
