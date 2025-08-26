package com.loadbalancer.dispatch.controller;

import com.loadbalancer.dispatch.model.Vehicle;
import com.loadbalancer.dispatch.service.DispatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/dispatch")
public class DispatchController {

    @Autowired
    private DispatchService dispatchService;

    @GetMapping("/plan")
    public ResponseEntity<Map<String, Object>> getDispatchPlan() {
        List<Vehicle> vehicles = dispatchService.getDispatchPlan();

        List<Map<String, Object>> dispatchPlan = new ArrayList<>();
        for (Vehicle v : vehicles) {
            Map<String, Object> vehicleMap = new LinkedHashMap<>();
            vehicleMap.put("vehicleId", v.getVehicleId());
            vehicleMap.put("totalLoad", v.getTotalLoad());
            vehicleMap.put("totalDistance", String.format("%.2f km", dispatchService.routeDistance(v)));

            List<Map<String, Object>> assignedOrdersList = new ArrayList<>();
            for (var order : v.getAssignedOrders()) {
                Map<String, Object> orderMap = new LinkedHashMap<>();
                orderMap.put("orderId", order.getOrderId());
                orderMap.put("latitude", order.getLatitude());
                orderMap.put("longitude", order.getLongitude());
                orderMap.put("address", order.getAddress());
                orderMap.put("packageWeight", order.getPackageWeight());
                orderMap.put("priority", order.getPriority());
                assignedOrdersList.add(orderMap);
            }
            vehicleMap.put("assignedOrders", assignedOrdersList);

            dispatchPlan.add(vehicleMap);
        }
        return ResponseEntity.ok(Map.of("dispatchPlan", dispatchPlan));
    }
}
