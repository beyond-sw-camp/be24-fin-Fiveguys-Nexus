package com.example.nexus.report;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/report")
@RestController
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
}
