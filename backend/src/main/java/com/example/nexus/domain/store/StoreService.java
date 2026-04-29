package com.example.nexus.domain.store;

import com.example.nexus.common.exception.BaseException;
import com.example.nexus.domain.store.model.Store;
import com.example.nexus.domain.store.model.StoreDto;
import com.example.nexus.domain.store.model.StoreInventory;
import com.example.nexus.domain.store.model.StoreInventoryDto;
import com.example.nexus.domain.user.UserRepository;
import com.example.nexus.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.nexus.common.model.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final StoreInventoryRepository storeInventoryRepository;
    private final UserRepository userRepository;

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

    public void storeReg(StoreDto.StoreRegReq dto) {
        // 이메일을 통한 가맹점 점주 체크
        User owner = userRepository.findByEmail(dto.getOwnerEmail()).orElseThrow(
                ()-> new BaseException(NOT_FOUND_USER));

        // 점주 중복 체크
        storeRepository.findByUserIdx(owner.getIdx())
                .ifPresent(s-> {
                    throw new BaseException(ALREADY_HAS_STORE);
                });

        // 가맹점 이름 중복 체크
        storeRepository.findByStoreName(dto.getStoreName())
                .ifPresent(s -> {
                    throw new BaseException(STORE_NAME_ALREADY_EXISTS);
                });

        // 가맹점 사업자 번호 중복 체크
        storeRepository.findByBusiness(dto.getBusiness())
                .ifPresent(s -> {
                    throw new BaseException(BUSINESS_NUMBER_ALREADY_EXISTS);
                });

        Store store = Store.builder()
                .storeName(dto.getStoreName())
                .address(dto.getAddress())
                .addressDetail(dto.getAddressDetail())
                .filePath(dto.getFilePath())
                .business(dto.getBusiness())
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .user(owner)
                .build();

        storeRepository.save(store);
    }
}
