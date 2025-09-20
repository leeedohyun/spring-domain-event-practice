package com.example.domain_event_practice.service.order;

import java.util.Arrays;
import java.util.List;

import com.example.domain_event_practice.domain.generic.Money;

public record Cart(Long userId, List<CartLineItem> items) {

    public Cart(Long userId, CartLineItem... items) {
        this(userId, Arrays.asList(items));
    }

    public record CartLineItem(Long productId, String name, Money price, int count) {

    }
}
