package com.example.nexus.headincome;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HeadIncomeService {
    private final HeadIncomeRepository headIncomeRepository;
}
