package com.example.domain_event_practice.service.order;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.domain_event_practice.domain.order.Order;
import com.example.domain_event_practice.domain.order.OrderLineItem;
import com.example.domain_event_practice.service.order.Cart.CartLineItem;

@Component
public class OrderMapper {

    public Order mapFrom(Cart cart) {
        return new Order(cart.userId(),
                UUID.randomUUID().toString(),
                cart.items()
                        .stream()
                        .map(this::toOrderLineItem)
                        .toList());
    }

    private OrderLineItem toOrderLineItem(CartLineItem item) {
        return new OrderLineItem(item.productId(), item.name(), item.price(), item.count());
    }
}
