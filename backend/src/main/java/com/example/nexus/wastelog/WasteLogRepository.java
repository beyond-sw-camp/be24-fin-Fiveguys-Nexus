package com.example.nexus.wastelog;

import com.example.nexus.wastelog.model.WasteLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WasteLogRepository extends JpaRepository<WasteLog, Long> {
}
