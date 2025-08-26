package com.loadbalancer.dispatch.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DistanceCalculatorTest {

    @Test
    void testCalculateDistanceKnownPoints() {
        // Example: Connaught Place and India Gate, Delhi
        double lat1 = 28.6315;
        double lon1 = 77.2167;
        double lat2 = 28.6129;
        double lon2 = 77.2295;

        double distance = DistanceCalculator.calculate(lat1, lon1, lat2, lon2);
        assertTrue(distance > 2 && distance < 3, "Distance should be between 2 and 3 km");
    }

    @Test
    void testCalculateDistanceSamePointIsZero() {
        double lat = 28.6315;
        double lon = 77.2167;
        double distance = DistanceCalculator.calculate(lat, lon, lat, lon);
        assertEquals(0, distance, 0.0001);
    }

    @Test
    void testNegativeCoordinates() {
        double lat1 = -28.6315;
        double lon1 = 77.2167;
        double lat2 = 28.6129;
        double lon2 = -77.2295;
        double distance = DistanceCalculator.calculate(lat1, lon1, lat2, lon2);
        assertTrue(distance > 0, "Distance across hemispheres should be positive");
    }

    @Test
    void testOrderOfPointsDoesntAffect() {
        double lat1 = 28.6315;
        double lon1 = 77.2167;
        double lat2 = 28.6129;
        double lon2 = 77.2295;
        double d1 = DistanceCalculator.calculate(lat1, lon1, lat2, lon2);
        double d2 = DistanceCalculator.calculate(lat2, lon2, lat1, lon1);
        assertEquals(d1, d2, 0.0001);
    }
}
