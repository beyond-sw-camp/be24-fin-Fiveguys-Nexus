package com.example.nexus.domain.menu;

import com.example.nexus.domain.menu.model.Menu;
import com.example.nexus.domain.menu.model.MenuDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;

    public MenuDto.MenuPageRes list(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Menu> result = menuRepository.findAll(pageRequest);

        return MenuDto.MenuPageRes.from(result);
    }

    public MenuDto.MenuItemListRes menuItemList(Long menuIdx) {
        Optional<Menu> res = menuRepository.findById(menuIdx);

        if(res.isPresent()){
            Menu menu = res.get();
            return MenuDto.MenuItemListRes.from(menu);
        }
        return null;
    }
}
