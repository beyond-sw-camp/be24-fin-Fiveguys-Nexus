package com.example.nexus.domain.store;

import com.example.nexus.domain.store.model.Store;
import com.example.nexus.domain.store.model.StoreDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;

    public List<StoreDto.StoreListRes> list() {
        List<Store> res = storeRepository.findAll();
        List<StoreDto.StoreListRes> result = new ArrayList<>();

        for(Store data: res){
            result.add(StoreDto.StoreListRes.from(data));
        }
        return result;
    }
}
