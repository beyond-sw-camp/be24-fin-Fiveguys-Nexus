package com.example.nexus.domain.head;

import com.example.nexus.domain.head.model.HeadInventory;
import com.example.nexus.domain.head.model.HeadInventoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HeadService {
    private final HeadIncomeRepository headIncomeRepository;
    private final HeadInventoryRepository headInventoryRepository;

    public List<HeadInventoryDto.ListRes> list() {
        List<HeadInventory> headInventoryList = headInventoryRepository.findAll();
        return headInventoryList.stream().map(HeadInventoryDto.ListRes::from).toList();
    }
}
