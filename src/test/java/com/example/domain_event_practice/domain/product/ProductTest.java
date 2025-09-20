package com.example.domain_event_practice.domain.product;

import org.junit.jupiter.api.Test;

import com.example.domain_event_practice.domain.generic.Money;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    void deductStock() {
        Product product = new Product("상품1", Money.wons(10000L), 10);

        product.deductStock(2);

        assertThat(product.getStock()).isEqualTo(8);
    }

    @Test
    void deductStock_StockLessThanQuantity() {
        Product product = new Product("상품1", Money.wons(10000L), 10);

        assertThatThrownBy(() -> product.deductStock(11))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고가 부족합니다.");
    }
}
