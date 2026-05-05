package com.example.nexus.domain.store;

import com.example.nexus.common.exception.BaseException;
import com.example.nexus.domain.store.model.Store;
import com.example.nexus.domain.store.model.StoreDto;
import com.example.nexus.domain.store.model.StoreInventory;
import com.example.nexus.domain.store.model.StoreInventoryDto;
import com.example.nexus.domain.user.UserRepository;
import com.example.nexus.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static com.example.nexus.common.model.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final StoreInventoryRepository storeInventoryRepository;
    private final UserRepository userRepository;

    // URL을 발행해주는 핵심 도구
    private final S3Presigner s3Presigner;

    @Value("${spring.cloud.aws.s3.store-bucket}")
    private String storeBucket;


    public List<StoreInventoryDto.ListRes> listByStoreIdx(Long storeIdx) {
        List<StoreInventory> inventoryList = storeInventoryRepository.findByStoreIdx(storeIdx);
        return inventoryList.stream().map(StoreInventoryDto.ListRes::from).toList();
    }

    @Transactional(readOnly = true)
    public StoreDto.StorePageRes storeList(String status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Store> result;

        if ("ACTIVE".equals(status)) {
            result = storeRepository.findByIsDeletedFalse(pageRequest);
        } else if ("CLOSED".equals(status)) {
            result = storeRepository.findByIsDeletedTrue(pageRequest);
        } else {
            result = storeRepository.findAll(pageRequest); // ALL인 경우
        }
        return StoreDto.StorePageRes.from(result);
    }

    public List<StoreDto.StoreSearchRes> searchByStoreName(StoreDto.StoreSearchReq reqDto) {
        String keyword = reqDto.getKeyword();
        String searchKeyword = keyword.trim();
        List<Store> res = storeRepository.findByStoreNameContainingIgnoreCase(searchKeyword);

        return res.stream().map(StoreDto.StoreSearchRes::from).toList();
    }

    @Transactional(readOnly = true)
    public StoreDto.StoreDetailListRes storeDetailList(Long storeIdx) {
        Optional<Store> res = storeRepository.findById(storeIdx);

        if(res.isPresent()){
            Store data = res.get();
            return StoreDto.StoreDetailListRes.from(data);
        }
        return null;
    }

    @Transactional
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
                .postcode(dto.getPostcode())
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

    // Presigned URL을 발급 받는 로직
    @Transactional
    public Map<String, String> getPresignedUrl(String fileName) {

        // 1. 경로 및 고유 파일명 생성 (기존 로직 유지)
        String path = createPath(fileName);

        // 2. S3 업로드 요청 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(storeBucket)
                .key(path)
                .build();

        // 3. Pre-signed 요청 설정 (유효시간 분)
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(putObjectRequest)
                .build();

        // 4. URL 생성 및 반환
        String url = s3Presigner.presignPutObject(presignRequest).url().toString();

        return Map.of(
                "url", url,
                "fileName", path // 프론트에서 나중에 DB에 저장할 때 필요하니까 같이 보내주는 게 좋아요!
        );
    }

    private String createPath(String fileName) {
        return UUID.randomUUID().toString() + "-nexus-" + fileName;
    }

    @Transactional
    public void storeUpdate(Long storeIdx, StoreDto.StoreUpdateReq dto) {
        // 가맹점 존재 여부
        Store store = storeRepository.findById(storeIdx)
                .orElseThrow(() ->{throw new BaseException(STORE_NOT_FOUND);
        });

        // 가맹점 폐업 여부
        if(store.isDeleted()){
            throw new BaseException(STORE_ALREADY_CLOSED);
        }

        // 가맹점 이름 중복 여부
        if(!store.getStoreName().equals(dto.getStoreName())){
            storeRepository.findByStoreName(dto.getStoreName())
                    .ifPresent(s ->{
                        throw new BaseException(STORE_NAME_ALREADY_EXISTS);
                    });
        }

        // 가맹점 업데이트
        store.update(dto);

        User owner = store.getUser();

        // 점주 탈퇴 여부 확인
        if(owner == null || owner.isDeleted()){
            throw new BaseException(NOT_FOUND_USER);
        }


        // 점주 이름, 이메일 업데이트
        if(owner != null){
            owner.updateOwner(dto.getOwnerName(),dto.getOwnerEmail());
        }
    }
}
