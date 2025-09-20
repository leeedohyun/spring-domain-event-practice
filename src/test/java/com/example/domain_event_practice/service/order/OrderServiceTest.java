package com.example.domain_event_practice.service.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain_event_practice.domain.generic.Money;
import com.example.domain_event_practice.domain.order.Order;
import com.example.domain_event_practice.domain.order.Order.OrderStatus;
import com.example.domain_event_practice.domain.order.OrderLineItem;
import com.example.domain_event_practice.domain.order.OrderPayedEvent;
import com.example.domain_event_practice.domain.order.OrderRepository;
import com.example.domain_event_practice.domain.product.Product;
import com.example.domain_event_practice.domain.product.ProductRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@RecordApplicationEvents
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

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

    @Test
    void pay(ApplicationEvents applicationEvents) {
        // given
        Long productId = 3L;
        Cart.CartLineItem cartLineItem = new Cart.CartLineItem(
                productId,
                "스프링 부트 마스터",
                Money.wons(35000L),
                3
        );

        Cart cart = new Cart(1L, cartLineItem);

        Order order = orderService.placeOrder(cart);

        // when
        orderService.payOrder(order.getId());

        // then
        Product updatedProduct = productRepository.findById(productId).orElseThrow();
        assertThat(updatedProduct.getStock()).isEqualTo(77L);

        // 이벤트 발행 확인
        // 1. OrderPlacedEvent 타입의 이벤트가 정확히 1번 발행되었는지 확인
        assertThat(applicationEvents.stream(OrderPayedEvent.class)).hasSize(1);

        // 2. 발행된 이벤트에서 Order 객체를 꺼내 상세 내용을 검증
        OrderPayedEvent publishedEvent = applicationEvents.stream(OrderPayedEvent.class)
                .findFirst()
                .orElseThrow();

        Order eventOrder = publishedEvent.order();
        assertThat(eventOrder).isNotNull();
        assertThat(eventOrder.getId()).isNotNull();
        assertThat(eventOrder.getUserId()).isEqualTo(cart.userId());
        assertThat(eventOrder.getStatus()).isEqualTo(OrderStatus.PAYED);

        // 3. Order 객체에 포함된 OrderLineItem의 내용까지 검증
        assertThat(eventOrder.getOrderLineItems()).hasSize(1);
        OrderLineItem eventLineItem = eventOrder.getOrderLineItems().get(0);
        assertThat(eventLineItem.getProductId()).isEqualTo(productId);
        assertThat(eventLineItem.getCount()).isEqualTo(3);
    }
}
