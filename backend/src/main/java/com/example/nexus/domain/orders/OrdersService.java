package com.example.nexus.domain.orders;

import com.example.nexus.domain.orders.model.Danger;
import com.example.nexus.domain.orders.model.DangerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrdersService {
    private final DangerRepository dangerRepository;

    public DangerDto.DangerRes find() {
        return dangerRepository.findById(1L)
                .map(DangerDto.DangerRes::from)
                .orElse(DangerDto.DangerRes.builder()
                        .ratio(200)
                        .period(3)
                        .build());
    }

    public void save(DangerDto.DangerReq req) {
        Danger danger = dangerRepository.findById(1L)
                .orElse(Danger.builder().build());
        danger.update(req.getRatio(), req.getPeriod());
        dangerRepository.save(danger);
    }
}
