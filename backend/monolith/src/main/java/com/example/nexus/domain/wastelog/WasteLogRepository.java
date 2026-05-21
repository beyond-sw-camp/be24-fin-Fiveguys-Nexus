package com.example.nexus.domain.wastelog;

import com.example.nexus.domain.wastelog.model.WasteLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WasteLogRepository extends JpaRepository<WasteLog, Long> {
    List<WasteLog> findAllByWasteDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
