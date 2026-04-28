package com.example.nexus.domain.orders;

import com.example.nexus.common.exception.BaseException;
import com.example.nexus.common.model.BaseResponseStatus;
import com.example.nexus.domain.orders.model.Danger;
import com.example.nexus.domain.orders.model.DangerDto;
import com.example.nexus.domain.orders.model.Orders;
import com.example.nexus.domain.orders.model.OrdersDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdersService {
    private final DangerRepository dangerRepository;
    private final OrdersRepository ordersRepository;

    public List<OrdersDto.OrdersRes> findAll() {
        return ordersRepository.findAll().stream()
                .map(OrdersDto.OrdersRes::from)
                .toList();
    }

    public OrdersDto.OrdersRes findById(Long ordersIdx) {
        Orders orders =  ordersRepository.findById(ordersIdx).orElseThrow(
                () -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));
        return OrdersDto.OrdersRes.from(orders);
    }

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
