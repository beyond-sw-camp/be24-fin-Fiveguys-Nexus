package com.example.nexus.newscollect;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/newscollect")
@RestController
@RequiredArgsConstructor
public class NewsCollectController {
    private final NewsCollectService newsCollectService;
}
