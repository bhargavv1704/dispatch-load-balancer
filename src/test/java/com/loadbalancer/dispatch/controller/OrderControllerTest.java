package com.loadbalancer.dispatch.controller;

import com.loadbalancer.dispatch.service.DispatchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DispatchService dispatchService;

    @Test
    void testAddOrders() throws Exception {
        String orderJson = """
                {
                  "orders": [
                    {
                      "orderId": "ORD001",
                      "latitude": 12.9716,
                      "longitude": 77.5946,
                      "address": "MG Road, Bangalore",
                      "packageWeight": 10,
                      "priority": "HIGH"
                    }
                  ]
                }
                """;
        mockMvc.perform(post("/api/dispatch/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void testAddOrdersWithMissingFieldsReturnsError() throws Exception {
        String invalidOrderJson = """
                {
                  "orders": [
                    {
                      "orderId": "ORD_X"
                      // missing fields
                    }
                  ]
                }
                """;
        mockMvc.perform(post("/api/dispatch/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidOrderJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddEmptyOrderListReturnsError() throws Exception {
        String emptyOrdersJson = "{ \"orders\": [] }";
        mockMvc.perform(post("/api/dispatch/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(emptyOrdersJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddOrdersWithInvalidWeightReturnsError() throws Exception {
        String orderJson = """
                {
                  "orders": [
                    {
                      "orderId": "ORD_BAD",
                      "latitude": 28.6,
                      "longitude": 77.2,
                      "address": "Test",
                      "packageWeight": -10,
                      "priority": "HIGH"
                    }
                  ]
                }
                """;
        mockMvc.perform(post("/api/dispatch/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
                .andExpect(status().isBadRequest());
    }
}
