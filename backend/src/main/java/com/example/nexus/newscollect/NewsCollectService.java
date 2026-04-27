package com.example.nexus.newscollect;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsCollectService {
    private final NewsCollectRepository newsCollectRepository;
}
