package com.example.nexus.domain.menu;

import com.example.nexus.common.model.BaseEntity;
import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.menu.model.MenuDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/menu")
@RestController
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    @GetMapping("/list")
    public ResponseEntity list(){
        List<MenuDto.MenuListRes> result =  menuService.list();
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
