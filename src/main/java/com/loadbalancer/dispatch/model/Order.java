package com.loadbalancer.dispatch.model;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Order {
    private String orderId;
    private double latitude;
    private double longitude;
    private String address;
    private double packageWeight;
    private Priority priority;
}
