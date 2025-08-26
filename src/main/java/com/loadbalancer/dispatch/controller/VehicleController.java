package com.loadbalancer.dispatch.controller;

import com.loadbalancer.dispatch.model.Vehicle;
import com.loadbalancer.dispatch.service.DispatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dispatch/vehicles")
public class VehicleController {

    @Autowired
    private DispatchService dispatchService;

    @PostMapping
    public ResponseEntity<Map<String, String>> addVehicles(@RequestBody Map<String, List<Vehicle>> vehiclesRequest) {
        List<Vehicle> vehicles = vehiclesRequest.get("vehicles");
        if (vehicles == null || vehicles.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Vehicles list is empty", "status", "failure"));
        }
        dispatchService.addVehicles(vehicles);
        return ResponseEntity.ok(Map.of("message", "Vehicle details accepted.", "status", "success"));
    }
}
