package com.example.domain_event_practice.domain.generic;

import java.math.BigDecimal;

import jakarta.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode
public class Money {

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    private BigDecimal amount;

    private Money(BigDecimal amount) {
        this.amount = amount;
    }

    public static Money wons(Long amount) {
        return new Money(BigDecimal.valueOf(amount));
    }
}
