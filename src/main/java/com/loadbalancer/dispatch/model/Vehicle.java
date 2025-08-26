package com.loadbalancer.dispatch.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Vehicle {
    private String vehicleId;
    private double capacity;
    private double currentLatitude;
    private double currentLongitude;
    private String currentAddress;
    private List<Order> assignedOrders = new ArrayList<>();

    // Utility to get current load assigned
    public double getTotalLoad() {
        if (this.assignedOrders == null || this.assignedOrders.isEmpty()) {
            return 0;
        }
        return this.assignedOrders.stream()
                .mapToDouble(Order::getPackageWeight)
                .sum();
    }

}
