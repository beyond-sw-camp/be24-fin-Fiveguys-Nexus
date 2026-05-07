package com.example.nexus.domain.store;

import com.example.nexus.common.exception.BaseException;
import com.example.nexus.domain.store.model.Store;
import com.example.nexus.domain.store.model.StoreDto;
import com.example.nexus.domain.store.model.StoreInventory;
import com.example.nexus.domain.store.model.StoreInventoryDto;
import com.example.nexus.domain.user.UserRepository;
import com.example.nexus.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import static com.example.nexus.common.model.BaseResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final StoreInventoryRepository storeInventoryRepository;
    private final UserRepository userRepository;

    // URL을 발행해주는 핵심 도구
    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.store-bucket}")
    private String storeBucket;


    public List<StoreInventoryDto.ListRes> listByStoreIdx(Long storeIdx) {
        List<StoreInventory> inventoryList = storeInventoryRepository.findByStoreIdx(storeIdx);
        return inventoryList.stream().map(StoreInventoryDto.ListRes::from).toList();
    }

    @Transactional(readOnly = true)
    public StoreDto.StorePageRes storeList(StoreDto.StoreSearchPagingReq req, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Store> result;

        String status = (req.getStatus() != null) ? req.getStatus().trim(): "";
        String keyword = (req.getKeyword() != null) ? req.getKeyword().trim() : "";
        boolean hasKeyword = !keyword.isEmpty();

        if ("ACTIVE".equals(status)) {
            result = hasKeyword
                    ? storeRepository.findByStatusAndKeyword(false, keyword, pageRequest)
                    : storeRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageRequest);
        } else if ("CLOSED".equals(status)) {
            result = hasKeyword
                    ? storeRepository.findByStatusAndKeyword(true, keyword, pageRequest)
                    : storeRepository.findByIsDeletedTrueOrderByClosedAtDesc(pageRequest);
        } else {
            result = hasKeyword
                    ? storeRepository.findByKeywordAll(keyword,pageRequest)
                    : storeRepository.findAllCustom(pageRequest);
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
        String newFilePath = dto.getFilePath();

        try{
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
        } catch (Exception e) {
            // 5. 예외 발생 시 S3 롤백 삭제
            // 트랜잭션 도중 에러가 나면 DB는 롤백되지만, S3에 업로드된 파일은 남으므로 삭제 처리
            if (newFilePath != null && !newFilePath.isEmpty()) {
                try {
                    deleteS3Object(newFilePath);
                } catch (Exception ignored) {
                    // 롤백 중 S3 삭제 실패는 무시하여 원래 예외를 보존합니다.
                }
            }

            // 6. 예외 다시 던지기
            if (e instanceof BaseException) throw (BaseException) e;
            throw new RuntimeException(e);
        }
    }

    // Presigned URL을 발급 받는 로직
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
        String newFilePath = dto.getFilePath();
        String oldFilePath = "";

        try {
            Store store = storeRepository.findById(storeIdx)
                    .orElseThrow(() -> new BaseException(STORE_NOT_FOUND));

            oldFilePath = store.getFilePath();

            if (store.isDeleted()) throw new BaseException(STORE_ALREADY_CLOSED);

            if (!store.getStoreName().equals(dto.getStoreName())) {
                storeRepository.findByStoreName(dto.getStoreName())
                        .ifPresent(s -> { throw new BaseException(STORE_NAME_ALREADY_EXISTS); });
            }

            User owner = store.getUser();
            if (owner == null || owner.isDeleted()) throw new BaseException(NOT_FOUND_USER);

            if (newFilePath != null && !newFilePath.equals(oldFilePath) && oldFilePath != null && !oldFilePath.isBlank()) {
                String finalOldFile = oldFilePath;
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        deleteS3Object(finalOldFile);
                    }
                });
            }

            store.update(dto);
            owner.updateOwner(dto.getOwnerName(), dto.getOwnerEmail());

        } catch (Exception e) {
            if (newFilePath != null && !newFilePath.isEmpty()) {
                try {
                    deleteS3Object(newFilePath);
                } catch (Exception ignored) {
                    // 롤백 중 S3 삭제 실패는 무시하여 원래 예외를 보존합니다.
                }
            }

            if (e instanceof BaseException) throw (BaseException) e;
            throw new RuntimeException(e);
        }
    }

    private void deleteS3Object(String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(storeBucket)
                    .key(key)
                    .build());
        } catch (Exception e) {
            // S3 삭제 실패 시 로그를 남기거나 무시하여 원래 예외가 전파되도록 합니다.
        }
    }

    public Long findStoreIdx(Long userIdx) {
        return storeRepository.findByUserIdx(userIdx).orElse(null).getIdx();
    }
}
