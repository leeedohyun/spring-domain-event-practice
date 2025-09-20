package com.example.domain_event_practice.service.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.domain_event_practice.domain.order.OrderLineItem;
import com.example.domain_event_practice.domain.order.OrderPayedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderPayedEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(OrderPayedEventHandler.class);

    private final ProductService productService;

    @EventListener(OrderPayedEvent.class)
    public void handleOrderPayedEvent(OrderPayedEvent event) {
        logger.info("OrderPayedEvent 이벤트를 처리합니다: {}", event.order().getId());

        for (OrderLineItem orderLineItem : event.order().getOrderLineItems()) {
            productService.deductStock(orderLineItem.getProductId(), orderLineItem.getCount());
        }
    }
}
