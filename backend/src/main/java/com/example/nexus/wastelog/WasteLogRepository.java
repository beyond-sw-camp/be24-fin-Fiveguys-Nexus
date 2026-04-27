package com.example.nexus.wastelog;

import com.example.nexus.wastelog.model.wasteLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WasteLogRepository extends JpaRepository<wasteLog, Long> {
}
