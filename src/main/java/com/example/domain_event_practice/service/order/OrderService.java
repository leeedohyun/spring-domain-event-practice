package com.example.domain_event_practice.service.order;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain_event_practice.domain.order.Order;
import com.example.domain_event_practice.domain.order.OrderRepository;
import com.example.domain_event_practice.domain.order.OrderValidator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderValidator orderValidator;

    @Transactional
    public Order placeOrder(Cart cart) {
        Order order = orderMapper.mapFrom(cart);
        order.place(orderValidator);

        return orderRepository.save(order);
    }

    @Transactional
    public void payOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다. id=" + orderId));
        order.payed();
        orderRepository.save(order);
    }
}
