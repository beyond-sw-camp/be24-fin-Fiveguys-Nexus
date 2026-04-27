package com.example.nexus.ordersitem;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrdersItemService {
    private final OrdersItemRepository orderItemRepository;
}
