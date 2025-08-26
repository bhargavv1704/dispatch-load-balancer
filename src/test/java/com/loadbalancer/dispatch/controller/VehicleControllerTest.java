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

@WebMvcTest(VehicleController.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DispatchService dispatchService;

    @Test
    void testAddVehicles() throws Exception {
        String vehicleJson = """
                {
                  "vehicles": [
                    {
                      "vehicleId": "VEH001",
                      "capacity": 100,
                      "currentLatitude": 12.9716,
                      "currentLongitude": 77.6413,
                      "currentAddress": "Indiranagar, Bangalore"
                    }
                  ]
                }
                """;
        mockMvc.perform(post("/api/dispatch/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(vehicleJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void testAddEmptyVehicleListReturnsError() throws Exception {
        String emptyVehiclesJson = "{ \"vehicles\": [] }";
        mockMvc.perform(post("/api/dispatch/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(emptyVehiclesJson))
                .andExpect(status().isBadRequest());
    }
}
