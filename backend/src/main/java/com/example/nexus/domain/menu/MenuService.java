package com.example.nexus.domain.menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;
}
