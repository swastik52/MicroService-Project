package com.microservice.orderservice.controller;


import com.microservice.orderservice.client.InventoryClient;
import com.microservice.orderservice.dto.OrderDto;
import com.microservice.orderservice.model.Order;
import com.microservice.orderservice.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreaker;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private InventoryClient inventoryClient;

    @Autowired
    private StreamBridge streamBridge;

    @Autowired
    private Resilience4JCircuitBreakerFactory circuitBreakerFactory;

    @Autowired
    private ExecutorService traceableExecutorService;

    @GetMapping
    public String getMessage() {
        return "test";
    }

    @PostMapping
    public String placeorder(@RequestBody OrderDto orderDto) {
        System.out.println(orderDto.toString());

        circuitBreakerFactory.configureExecutorService(traceableExecutorService);

        Resilience4JCircuitBreaker circuitBreaker = circuitBreakerFactory.create("inventory");
        Supplier<Boolean> booleanSupplier = () -> orderDto.getOrderLineItemsList().stream()
                .allMatch(orderLineItems -> {
                    log.info("Making call to inventory service  for Skucode {}",orderLineItems.getSkuCode());
                    return inventoryClient.checkStock(orderLineItems.getSkuCode());
                });
        boolean allProductsInStock = circuitBreaker.run(booleanSupplier, throwable -> handelErrorCase());

        if (allProductsInStock) {
            Order order = new Order();
            order.setOrderLineItems(orderDto.getOrderLineItemsList());
            order.setOrderNumber(UUID.randomUUID().toString());

            orderRepository.save(order);
            log.info("Sending Order Details with order id {} to Notification Service",order.getId());
//          streamBridge.send("notificationEventSupplier-out-0", order.getId());
            streamBridge.send("notificationEventSupplier-out-0", MessageBuilder.withPayload(order.getId()).build());
            return "Order Placed Successfully";
        } else {
            return "Order Failed! one of teh product in the orderlist is not in stock";
        }
    }

    private Boolean handelErrorCase() {
        return false;
    }
}
