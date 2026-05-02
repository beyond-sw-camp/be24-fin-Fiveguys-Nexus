package com.example.nexus.domain.menu;

import com.example.nexus.domain.menu.model.Menu;
import com.example.nexus.domain.menu.model.MenuDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;

    public List<MenuDto.MenuListRes> list() {
        List<Menu> res = menuRepository.findAll();
        List<MenuDto.MenuListRes> result = new ArrayList<>();

        for(Menu data : res){
            result.add(MenuDto.MenuListRes.from(data));
        }
        return result;
    }
}
