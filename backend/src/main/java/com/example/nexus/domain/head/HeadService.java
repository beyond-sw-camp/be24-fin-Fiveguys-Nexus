package com.example.nexus.domain.head;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HeadService {
    private final HeadIncomeRepository headIncomeRepository;
}
