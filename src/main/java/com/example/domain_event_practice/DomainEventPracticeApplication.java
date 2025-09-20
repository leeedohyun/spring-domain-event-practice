package com.example.domain_event_practice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.domain_event_practice.domain.generic.Money;
import com.example.domain_event_practice.domain.order.Order;
import com.example.domain_event_practice.service.order.Cart;
import com.example.domain_event_practice.service.order.Cart.CartLineItem;
import com.example.domain_event_practice.service.order.OrderService;

@SpringBootApplication
public class DomainEventPracticeApplication implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(DomainEventPracticeApplication.class);

	private final OrderService orderService;

	public DomainEventPracticeApplication(OrderService orderService) {
		this.orderService = orderService;
	}

	public static void main(String[] args) {
		LOGGER.info("STARTING THE APPLICATION");
		SpringApplication.run(DomainEventPracticeApplication.class, args);
		LOGGER.info("APPLICATION FINISHED");
	}

	@Override
	public void run(String... args) {
		Cart cart = new Cart(1L,
				new CartLineItem(1L, "스프링 부트 마스터", Money.wons(35000L), 1),
				new CartLineItem(2L, "JPA 프로그래밍 입문", Money.wons(42000L), 2));

		Order order = orderService.placeOrder(cart);

		orderService.payOrder(order.getId());
	}
}
