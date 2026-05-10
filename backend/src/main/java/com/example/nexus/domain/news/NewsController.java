package com.example.nexus.domain.news;

import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.news.model.NewsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    // [본사/매장] 날짜별 뉴스 요약 목록 조회
    // storeIdx 없으면 본사(HQ) 뉴스, 있으면 해당 매장 뉴스
    @GetMapping
    public ResponseEntity<BaseResponse<List<NewsDto.SummaryListItem>>> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long storeIdx) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(BaseResponse.success(newsService.listSummaries(targetDate, storeIdx)));
    }

    // [본사/매장] 뉴스 요약 상세 조회
    @GetMapping("/{idx}")
    public ResponseEntity<BaseResponse<NewsDto.SummaryDetail>> detail(@PathVariable Long idx) {
        return ResponseEntity.ok(BaseResponse.success(newsService.getSummary(idx)));
    }

    // [본사] 뉴스 수집 수동 트리거
    @PostMapping("/collect")
    public ResponseEntity<BaseResponse<NewsDto.CollectRunResult>> collect() {
        return ResponseEntity.ok(BaseResponse.success(newsService.runDailyPipeline()));
    }
}
