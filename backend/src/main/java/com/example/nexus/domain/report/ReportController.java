package com.example.nexus.domain.report;


import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.report.model.ReportDto;
import com.example.nexus.domain.user.model.AuthUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequestMapping("/report")
@RestController
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    // 사용자 메세지 통신
    @PostMapping("/generate")
    public ResponseEntity<BaseResponse> requestReport(
            @AuthenticationPrincipal AuthUserDetails userDetails,
            @Valid @RequestBody ReportDto.ChatRequest request) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        Long userIdx = userDetails.getIdx();
        String result = reportService.createAndSaveReport(userIdx, request.message());
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 보고서 조회
    @GetMapping("/list")
    public ResponseEntity<BaseResponse> reportList(
            @AuthenticationPrincipal AuthUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){

        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        Long userIdx = userDetails.getIdx();
        ReportDto.ReportPageRes result = reportService.reportList(userIdx, page, size);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

}
