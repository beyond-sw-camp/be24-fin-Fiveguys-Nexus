package com.example.nexus.headincome;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/headincome")
@RestController
@RequiredArgsConstructor
public class HeadIncomeController {
    private final HeadIncomeService headIncomeService;
}
