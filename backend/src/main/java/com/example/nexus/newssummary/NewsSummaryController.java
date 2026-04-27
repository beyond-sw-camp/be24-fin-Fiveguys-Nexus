package com.example.nexus.newssummary;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/newssummary")
@RestController
@RequiredArgsConstructor
public class NewsSummaryController {
    private final NewsSummaryService newsSummaryService;
}
