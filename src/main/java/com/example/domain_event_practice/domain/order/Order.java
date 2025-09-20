package com.example.domain_event_practice.domain.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.springframework.data.domain.AbstractAggregateRoot;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ORDERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order extends AbstractAggregateRoot<Order> {

    public enum OrderStatus { ORDERED, PAYED, DELIVERED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String name;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime orderedTime;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="ORDER_ID")
    private List<OrderLineItem> orderLineItems = new ArrayList<>();

    public Order(Long userId, String name, List<OrderLineItem> items) {
        this(userId, name, null, LocalDateTime.now(), items);
    }

    public Order(Long userId, String name, OrderStatus status, LocalDateTime orderedTime, List<OrderLineItem> orderLineItems) {
        this.userId = userId;
        this.name = name;
        this.status = status;
        this.orderedTime = orderedTime;
        this.orderLineItems.addAll(orderLineItems);
    }

    public void place(OrderValidator orderValidator) {
        orderValidator.validate(this);

        ordered();
    }

    public void payed() {
        this.status = OrderStatus.PAYED;

        registerEvent(new OrderPayedEvent(this));
    }

    public List<Long> getProductIds() {
        return orderLineItems.stream()
                .map(OrderLineItem::getProductId)
                .toList();
    }

    private void ordered() {
        this.status = OrderStatus.ORDERED;
    }
}
