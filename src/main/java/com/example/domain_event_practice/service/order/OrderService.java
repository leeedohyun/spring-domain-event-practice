package com.example.domain_event_practice.service.order;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain_event_practice.domain.order.Order;
import com.example.domain_event_practice.domain.order.OrderRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public void placeOrder(Cart cart) {
        Order order = orderMapper.mapFrom(cart);
        order.place();
        orderRepository.save(order);
    }
}
