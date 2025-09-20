package com.example.domain_event_practice.domain.order;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import com.example.domain_event_practice.domain.generic.Money;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderLineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private String productName;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "product_price"))
    private Money productPrice;

    private int count;

    public OrderLineItem(Long productId, String productName, Money productPrice, int count) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.count = count;
    }
}
