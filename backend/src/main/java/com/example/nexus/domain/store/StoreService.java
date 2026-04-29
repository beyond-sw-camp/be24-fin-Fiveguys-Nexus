package com.example.nexus.domain.store;

import com.example.nexus.domain.store.model.Store;
import com.example.nexus.domain.store.model.StoreDto;
import com.example.nexus.domain.store.model.StoreInventory;
import com.example.nexus.domain.store.model.StoreInventoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final StoreInventoryRepository storeInventoryRepository;

    public List<StoreInventoryDto.ListRes> listByStoreIdx(Long storeIdx) {
        List<StoreInventory> inventoryList = storeInventoryRepository.findByStoreIdx(storeIdx);
        return inventoryList.stream().map(StoreInventoryDto.ListRes::from).toList();
    }


    public List<StoreDto.StoreListRes> storeList() {
        List<Store> res = storeRepository.findAll();
        List<StoreDto.StoreListRes> result = new ArrayList<>();

        for(Store data: res){
            result.add(StoreDto.StoreListRes.from(data));
        }
        return result;
    }

    public List<StoreDto.StoreSearchRes> searchByStoreName(StoreDto.StoreSearchReq reqDto) {
        String keyword = reqDto.getKeyword();
        String searchKeyword = keyword.trim();
        List<Store> res = storeRepository.findByStoreNameContainingIgnoreCase(searchKeyword);

        return res.stream().map(StoreDto.StoreSearchRes::from).toList();
    }
      
    public StoreDto.StoreDetailListRes storeDetailList(Long storeIdx) {
        Optional<Store> res = storeRepository.findById(storeIdx);

        if(res.isPresent()){
            Store data = res.get();
            return StoreDto.StoreDetailListRes.from(data);
        }
        return null;
    }
}
