package com.example.nexus.domain.news;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/news")
@RestController
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsCollectService;
}
