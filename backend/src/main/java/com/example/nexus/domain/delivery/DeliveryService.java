package com.example.nexus.domain.delivery;

import com.example.nexus.common.enums.DeliveryStatus;
import com.example.nexus.domain.delivery.model.DeliveryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;

    public List<DeliveryDto> getDeliveriesByHead(
            String storeName, DeliveryStatus status, Integer year, Integer month, Integer day) {
        return deliveryRepository.findAllByFilters(storeName, status, year, month, day)
                .stream()
                .map(DeliveryDto::from)
                .collect(Collectors.toList());
    }

    public List<DeliveryDto> getDeliveriesForStore(Long storeIdx) {
        return deliveryRepository.findAllByOrdersStoreIdx(storeIdx)
                .stream()
                .map(DeliveryDto::from)
                .collect(Collectors.toList());
    }
}
