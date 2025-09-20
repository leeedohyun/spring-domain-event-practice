package com.example.domain_event_practice.service.product;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain_event_practice.domain.product.Product;
import com.example.domain_event_practice.domain.product.ProductRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public void deductStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. ID: " + productId));

        product.deductStock(quantity);
    }
}
