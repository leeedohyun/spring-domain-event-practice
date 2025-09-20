package com.example.domain_event_practice.domain.order;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.domain_event_practice.domain.generic.Money;
import com.example.domain_event_practice.domain.product.Product;
import com.example.domain_event_practice.domain.product.ProductRepository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class OrderValidatorTest {

    private OrderValidator validator;

    @BeforeEach
    public void setUp() {
        validator = new OrderValidator(mock(ProductRepository.class));
    }

    @Test
    public void validateFailEmptyProducts() {
        Order order = new Order(1L, "주문", Collections.emptyList());

        assertThatThrownBy(() -> validator.validate(order, new HashMap<>()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void validateFailInsufficientStock() {
        OrderLineItem orderLineItem = new OrderLineItem(1L, "상품1", Money.wons(10000L), 2);
        Order order = new Order(1L, "주문", List.of(orderLineItem));
        Map<Long, Product> products = new HashMap<>() {{ put(1L, new Product(1L, "상품1", Money.wons(10000L), 1)); }};

        assertThatThrownBy(() -> validator.validate(order, products))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
