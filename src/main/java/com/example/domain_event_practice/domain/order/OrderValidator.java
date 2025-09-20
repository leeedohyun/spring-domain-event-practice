package com.example.domain_event_practice.domain.order;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.domain_event_practice.domain.product.Product;
import com.example.domain_event_practice.domain.product.ProductRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderValidator {

    private final ProductRepository productRepository;

    public void validate(Order order) {
        validate(order, getProducts(order));
    }

    void validate(Order order, Map<Long, Product> products) {
        if (order.getOrderLineItems().isEmpty()) {
            throw new IllegalArgumentException("주문 상품이 없습니다.");
        }

        for (OrderLineItem orderLineItem : order.getOrderLineItems()) {
            validateOrderLineItem(orderLineItem, products);
        }
    }

    private void validateOrderLineItem(OrderLineItem orderLineItem, Map<Long, Product> products) {
        Product product = products.get(orderLineItem.getProductId());

        if (product.getStock() < orderLineItem.getCount()) {
            throw new IllegalArgumentException("재고가 부족한 상품이 있습니다.");
        }
    }

    private Map<Long, Product> getProducts(Order order) {
        return productRepository.findAllById(order.getProductIds())
                .stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
    }
}
