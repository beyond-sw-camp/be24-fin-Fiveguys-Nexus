package com.example.statistics;

import com.example.statistics.domain.daily.DailyDumpService;
import com.example.statistics.domain.daily.dto.DumpResult;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 운영 도구 + 개발 검증용 내부 API.
 * 자동 스케줄러 실패 시 수동 dump, 누락 복구 시 catch-up 등에 활용.
 *
 * ⚠️ 운영 환경에서는 K8s NetworkPolicy 또는 Spring Security 로 외부 접근 차단 필수.
 */
@RestController
@RequestMapping("/internal/statistics")
@RequiredArgsConstructor
public class InternalStatisticsController {
    private final DailyDumpService dailyDumpService;

    /**
     * 특정 날짜의 Redis 일별 집계를 MariaDB 에 수동 dump.
     *
     * @param date 처리할 날짜 (생략 시 어제)
     * @return DumpResult — status (SUCCESS/FAILED/SKIPPED), 처리 건수, 소요 시간 등
     */
    @PostMapping("/dailyDump")
    public ResponseEntity<DumpResult> manualDailyDump(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate target = (date != null) ? date : LocalDate.now().minusDays(1);
        DumpResult result = dailyDumpService.dump(target);
        return ResponseEntity.ok(result);
    }
}
