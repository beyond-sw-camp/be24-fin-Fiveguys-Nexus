package com.example.nexus.domain.report;


import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.report.model.ReportDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/report")
@RestController
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    // 사용자 메세지 통신
    @PostMapping("/generate")
    public ResponseEntity<BaseResponse> requestReport(@Valid @RequestBody ReportDto.ChatRequest request) {
        String result = reportService.handleChatbotRequest(request.message());
        return ResponseEntity.ok(BaseResponse.success(result));
    }

}
