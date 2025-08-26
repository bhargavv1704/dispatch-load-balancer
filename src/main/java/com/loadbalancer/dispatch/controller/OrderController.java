package com.loadbalancer.dispatch.controller;

import com.loadbalancer.dispatch.model.Order;
import com.loadbalancer.dispatch.service.DispatchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dispatch/orders")
public class OrderController {

    @Autowired
    private DispatchService dispatchService;

    @PostMapping
    public ResponseEntity<Map<String, String>> addOrders(@RequestBody Map<String, List<Order>> ordersRequest) {
        List<Order> orders = ordersRequest.get("orders");
        if (orders == null || orders.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Orders list is empty", "status", "failure"));
        }

        for (Order order : orders) {
            if (order.getPackageWeight() <= 0) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "Invalid order: packageWeight must be positive for orderId " + order.getOrderId(),
                        "status", "failure"));
            }
            if (order.getLatitude() < -90 || order.getLatitude() > 90 ||
                    order.getLongitude() < -180 || order.getLongitude() > 180) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "Invalid latitude/longitude for orderId " + order.getOrderId(),
                        "status", "failure"));
            }
            if (order.getOrderId() == null || order.getOrderId().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "Invalid order: orderId is missing or blank",
                        "status", "failure"));
            }
        }
        dispatchService.addOrders(orders);
        return ResponseEntity.ok(Map.of("message", "Delivery orders accepted.", "status", "success"));
    }
}
