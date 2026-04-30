package com.example.nexus.domain.wastelog;

import com.example.nexus.domain.wastelog.model.WasteLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WasteLogRepository extends JpaRepository<WasteLog, Long> {
}
