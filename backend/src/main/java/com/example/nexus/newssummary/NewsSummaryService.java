package com.example.nexus.newssummary;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsSummaryService {
    private final NewsSummaryRepository newsSummaryRepository;
}
