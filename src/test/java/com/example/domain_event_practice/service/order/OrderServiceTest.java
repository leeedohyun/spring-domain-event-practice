package com.example.domain_event_practice.service.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain_event_practice.domain.generic.Money;
import com.example.domain_event_practice.domain.order.Order;
import com.example.domain_event_practice.domain.order.Order.OrderStatus;
import com.example.domain_event_practice.domain.order.OrderRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void place() {
        // given
        Long productId = 3L;
        Cart.CartLineItem cartLineItem = new Cart.CartLineItem(
                productId,
                "스프링 부트 마스터",
                Money.wons(35000L),
                3
        );

        Cart cart = new Cart(1L, cartLineItem);

        // when
        orderService.placeOrder(cart);

        // then
        Order order = orderRepository.findById(1L).orElseThrow();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ORDERED);
    }
}
