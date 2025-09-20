package com.example.domain_event_practice.domain.order;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.domain_event_practice.domain.generic.Money;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Test
    void place() {
        OrderLineItem orderLineItem = new OrderLineItem(1L, "상품1", Money.wons(10000L), 2);
        Order order = new Order(1L, "주문", List.of(orderLineItem));

        order.place();

        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.ORDERED);
    }
}
