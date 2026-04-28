package com.example.nexus.domain.head;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/head")
@RestController
@RequiredArgsConstructor
public class HeadController {
    private final HeadService headIncomeService;
}
