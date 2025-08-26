package com.loadbalancer.dispatch.service;

import com.loadbalancer.dispatch.model.Order;
import com.loadbalancer.dispatch.model.Priority;
import com.loadbalancer.dispatch.model.Vehicle;
import com.loadbalancer.dispatch.repository.OrderRepository;
import com.loadbalancer.dispatch.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DispatchServiceTest {

    @Autowired
    private DispatchService dispatchService;

    @BeforeEach
    public void setup() {
        dispatchService.clearState();
    }

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private VehicleRepository vehicleRepository;

    @Test
    void simpleSanityTest() {
        assertEquals(2, 1 + 1);
    }

    @Test
    void cannotAssignOverweightOrderToVehicle() {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId("VEH001");
        vehicle.setCapacity(5);
        vehicle.setCurrentLatitude(12.9716);
        vehicle.setCurrentLongitude(77.6413);
        vehicle.setCurrentAddress("Indiranagar, Bangalore");

        Order order = new Order();
        order.setOrderId("ORD001");
        order.setLatitude(12.9716);
        order.setLongitude(77.5946);
        order.setAddress("MG Road, Bangalore");
        order.setPackageWeight(10); // overweight
        order.setPriority(Priority.HIGH);

        dispatchService.addVehicles(List.of(vehicle));
        dispatchService.addOrders(List.of(order));
        List<Vehicle> plan = dispatchService.getDispatchPlan();
        Vehicle v = plan.get(0);
        assertTrue(v.getAssignedOrders() == null || v.getAssignedOrders().isEmpty());
    }

    @Test
    void duplicateOrderIdsHandledGracefully() {
        Order order1 = new Order();
        order1.setOrderId("ORD_DUP");
        order1.setLatitude(12.9716);
        order1.setLongitude(77.5946);
        order1.setAddress("MG Road, Bangalore");
        order1.setPackageWeight(10);
        order1.setPriority(Priority.HIGH);

        Order order2 = new Order();
        order2.setOrderId("ORD_DUP");
        order2.setLatitude(13.0827);
        order2.setLongitude(80.2707);
        order2.setAddress("Anna Salai, Chennai");
        order2.setPackageWeight(15);
        order2.setPriority(Priority.MEDIUM);

        dispatchService.addOrders(List.of(order1, order2));
        assertDoesNotThrow(() -> dispatchService.getDispatchPlan());
    }

    @Test
    void performanceWithLargeOrderSet() {
        List<Vehicle> vehicles = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Vehicle v = new Vehicle();
            v.setVehicleId("V" + i);
            v.setCapacity(1000);
            v.setCurrentLatitude(28.6 + i * 0.1);
            v.setCurrentLongitude(77.2 + i * 0.1);
            v.setCurrentAddress("Test City " + i);
            vehicles.add(v);
        }
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            Order o = new Order();
            o.setOrderId("O" + i);
            o.setLatitude(28.6 + (i % 10) * 0.1);
            o.setLongitude(77.2 + (i % 10) * 0.1);
            o.setAddress("Destination " + i);
            o.setPackageWeight(10 + (i % 50));
            o.setPriority(Priority.LOW);
            orders.add(o);
        }

        dispatchService.addVehicles(vehicles);
        dispatchService.addOrders(orders);
        List<Vehicle> plan = dispatchService.getDispatchPlan();
        int totalAssignedOrders = plan.stream().mapToInt(v -> v.getAssignedOrders().size()).sum();
        assertTrue(totalAssignedOrders > 0, "Some orders should be assigned in large dataset test");
    }

    @Test
    void performanceWithVeryLargeDatasetWithinTimeLimit() {
        List<Vehicle> vehicles = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Vehicle v = new Vehicle();
            v.setVehicleId("VEH" + i);
            v.setCapacity(1000);
            v.setCurrentLatitude(28.7 + i);
            v.setCurrentLongitude(77.2 + i);
            v.setCurrentAddress("Bulk City " + i);
            vehicles.add(v);
        }
        Priority[] priorities = Priority.values();
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Order o = new Order();
            o.setOrderId("ORD" + i);
            o.setLatitude(28.7 + (i % 10) * 0.01);
            o.setLongitude(77.1 + (i % 10) * 0.01);
            o.setAddress("Bulk Destination " + i);
            o.setPackageWeight(10 + (i % 20));
            o.setPriority(priorities[i % priorities.length]);
            orders.add(o);
        }
        dispatchService.addVehicles(vehicles);
        dispatchService.addOrders(orders);

        long start = System.currentTimeMillis();
        dispatchService.getDispatchPlan();
        long elapsed = System.currentTimeMillis() - start;
        assertTrue(elapsed < 5000,
                "Large dataset dispatch must complete in under 5 seconds but took " + elapsed + " ms");
    }

    // ADDITIONAL SUGGESTED TESTS:

    @Test
    void highPriorityOrdersAssignedFirst() {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId("HIGHV1");
        vehicle.setCapacity(20);
        vehicle.setCurrentLatitude(29.0);
        vehicle.setCurrentLongitude(77.0);
        vehicle.setCurrentAddress("HQ");

        Order high = new Order();
        high.setOrderId("HIGH1");
        high.setLatitude(29.1);
        high.setLongitude(77.1);
        high.setAddress("HighAddr");
        high.setPackageWeight(10);
        high.setPriority(Priority.HIGH);

        Order low = new Order();
        low.setOrderId("LOW1");
        low.setLatitude(29.2);
        low.setLongitude(77.2);
        low.setAddress("LowAddr");
        low.setPackageWeight(10);
        low.setPriority(Priority.LOW);

        dispatchService.addVehicles(List.of(vehicle));
        dispatchService.addOrders(List.of(low, high));
        List<Vehicle> plan = dispatchService.getDispatchPlan();

        List<Order> assigned = plan.get(0).getAssignedOrders();
        assertTrue(assigned.get(0).getPriority() == Priority.HIGH);
    }

    @Test
    void noVehicleNoAssignment() {
        Order order = new Order();
        order.setOrderId("ORD001");
        order.setLatitude(12.9716);
        order.setLongitude(77.5946);
        order.setAddress("MG Road, Bangalore");
        order.setPackageWeight(10);
        order.setPriority(Priority.HIGH);

        dispatchService.addOrders(List.of(order));
        List<Vehicle> plan = dispatchService.getDispatchPlan();
        for (Vehicle v : plan) {
            assertTrue(v.getAssignedOrders() == null || v.getAssignedOrders().isEmpty());
        }
    }
}
