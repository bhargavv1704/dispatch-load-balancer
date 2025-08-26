package com.loadbalancer.dispatch.service;

import com.loadbalancer.dispatch.entity.OrderEntity;
import com.loadbalancer.dispatch.entity.VehicleEntity;
import com.loadbalancer.dispatch.model.Order;
import com.loadbalancer.dispatch.model.Vehicle;
import com.loadbalancer.dispatch.repository.OrderRepository;
import com.loadbalancer.dispatch.repository.VehicleRepository;
import com.loadbalancer.dispatch.util.DistanceCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class DispatchService {

    private final Map<String, Order> orders = new ConcurrentHashMap<>();
    private final Map<String, Vehicle> vehicles = new ConcurrentHashMap<>();

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public void addOrders(List<Order> newOrders) {
        List<OrderEntity> entities = newOrders.stream().map(order -> {
            OrderEntity e = new OrderEntity();
            e.setOrderId(order.getOrderId());
            e.setLatitude(order.getLatitude());
            e.setLongitude(order.getLongitude());
            e.setAddress(order.getAddress());
            e.setPackageWeight((int) order.getPackageWeight());
            e.setPriority(order.getPriority().toString());
            return e;
        }).collect(Collectors.toList());
        orderRepository.saveAll(entities);

        for (Order order : newOrders) {
            orders.put(order.getOrderId(), order);
        }
    }

    public void clearState() {
        orders.clear();
        vehicles.clear();
    }

    public void addVehicles(List<Vehicle> newVehicles) {
        List<VehicleEntity> entities = newVehicles.stream().map(vehicle -> {
            VehicleEntity e = new VehicleEntity();
            e.setVehicleId(vehicle.getVehicleId());
            e.setCapacity((int) vehicle.getCapacity());
            e.setCurrentLatitude(vehicle.getCurrentLatitude());
            e.setCurrentLongitude(vehicle.getCurrentLongitude());
            e.setCurrentAddress(vehicle.getCurrentAddress());
            return e;
        }).collect(Collectors.toList());
        vehicleRepository.saveAll(entities);

        for (Vehicle vehicle : newVehicles) {
            vehicles.put(vehicle.getVehicleId(), vehicle);
        }
    }

    public List<Vehicle> getDispatchPlan() {
        // Step 1: Clear assignments
        vehicles.values().forEach(v -> {
            if (v.getAssignedOrders() == null)
                v.setAssignedOrders(new ArrayList<>());
            else
                v.getAssignedOrders().clear();
        });

        // Step 2: Priority sort (HIGH first)
        List<Order> orderList = new ArrayList<>(orders.values());
        orderList.sort(Comparator.comparingInt(o -> o.getPriority().ordinal())); // HIGH=0, MEDIUM=1, LOW=2

        Set<String> assignedOrderIds = new HashSet<>();
        for (Order order : orderList) {
            if (assignedOrderIds.contains(order.getOrderId()))
                continue;

            Vehicle bestVehicle = null;
            double minDist = Double.MAX_VALUE;

            for (Vehicle vehicle : vehicles.values()) {
                double remainingCapacity = vehicle.getCapacity();
                for (Order assigned : vehicle.getAssignedOrders()) {
                    remainingCapacity -= assigned.getPackageWeight();
                }
                if (remainingCapacity < order.getPackageWeight())
                    continue;

                double distBefore = routeDistance(vehicle);
                vehicle.getAssignedOrders().add(order);
                double distAfter = routeDistance(vehicle);
                vehicle.getAssignedOrders().remove(vehicle.getAssignedOrders().size() - 1);

                double delta = distAfter - distBefore;
                if (delta < minDist) {
                    minDist = delta;
                    bestVehicle = vehicle;
                }
            }

            if (bestVehicle != null) {
                bestVehicle.getAssignedOrders().add(order);
                assignedOrderIds.add(order.getOrderId());
            }
        }

        // Step 3: Sort each vehicle's assignedOrders by priority (HIGH first)
        vehicles.values().forEach(v -> {
            List<Order> assigned = v.getAssignedOrders();
            if (assigned != null)
                assigned.sort(Comparator.comparingInt(o -> o.getPriority().ordinal()));
        });

        return new ArrayList<>(vehicles.values());
    }

    public double routeDistance(Vehicle vehicle) {
        List<Order> assignedOrders = vehicle.getAssignedOrders();
        if (assignedOrders == null || assignedOrders.isEmpty())
            return 0;
        double distance = 0.0;
        double prevLat = vehicle.getCurrentLatitude();
        double prevLng = vehicle.getCurrentLongitude();
        for (Order order : assignedOrders) {
            distance += DistanceCalculator.calculate(prevLat, prevLng, order.getLatitude(), order.getLongitude());
            prevLat = order.getLatitude();
            prevLng = order.getLongitude();
        }
        return distance;
    }
}
