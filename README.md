# dispatch-load-balancer

A Spring Boot REST API that assigns delivery orders to vehicles optimally, based on priority, capacity, and minimum travel distance (by Haversine formula).

---

## Features

- **Java/Spring Boot** backend, no UI
- **REST APIs** only – test via Postman or curl
- **Order-to-Vehicle Assignment** with:
  - Priority-based allocation (HIGH, then MEDIUM, then LOW)
  - Capacity respected (no overload)
  - Distance minimization
- **Error handling**, input validation, modular code
- **Comprehensive JUnit tests (all passing)**
- **Easy local or Docker run**
- **Full documentation and ready Postman/curl payloads**

---

## API Documentation

### 1. Add Delivery Orders

**POST** `/api/dispatch/orders`

**Request Example:**
{
"orders": [
{
"orderId": "ORD001",
"latitude": 12.9716,
"longitude": 77.5946,
"address": "MG Road, Bangalore, Karnataka, India",
"packageWeight": 10,
"priority": "HIGH"
}
]
}
**Success Response:**
{
"message": "Delivery orders accepted.",
"status": "success"
}

---

### 2. Add Vehicles

**POST** `/api/dispatch/vehicles`

**Request Example:**
{
"vehicles": [
{
"vehicleId": "VEH001",
"capacity": 100,
"currentLatitude": 12.9716,
"currentLongitude": 77.6413,
"currentAddress": "Indiranagar, Bangalore, Karnataka, India"
}
]
}
**Success Response:**
{
"message": "Vehicle details accepted.",
"status": "success"
}

---

### 3. Retrieve Dispatch Plan

**GET** `/api/dispatch/plan`

**Response Example:**
{
"dispatchPlan": [
{
"vehicleId": "VEH001",
"totalLoad": 10,
"totalDistance": "5 km",
"assignedOrders": [
{
"orderId": "ORD001",
"latitude": 12.9716,
"longitude": 77.5946,
"address": "MG Road, Bangalore, Karnataka, India",
"packageWeight": 10,
"priority": "HIGH"
}
]
}
]
}

---

## Sample Inputs

**Orders Example:**
[
{ "orderId": "ORD001", "latitude": 28.6139, "longitude": 77.2090, "address": "Connaught Place, Delhi, India", "packageWeight": 15, "priority": "HIGH" },
{ "orderId": "ORD002", "latitude": 28.6139, "longitude": 77.2090, "address": "Connaught Place, Delhi, India", "packageWeight": 10, "priority": "MEDIUM" }
]

**Vehicles Example:**
[
{ "vehicleId": "VEH001", "capacity": 100, "currentLatitude": 28.7041, "currentLongitude": 77.1025, "currentAddress": "Karol Bagh, Delhi, India" },
{ "vehicleId": "VEH002", "capacity": 80, "currentLatitude": 28.5355, "currentLongitude": 77.3910, "currentAddress": "Sector 18, Noida, Uttar Pradesh, India" }
]

---

## How to Run (Locally with Maven)

1. **Clone the repo:**
    ```
    git clone https://github.com/bhargavv1704/dispatch-load-balancer.git
    cd dispatch-load-balancer
    ```
2. **Build:**
    ```
    mvn clean install
    ```
3. **Run:**
    ```
    mvn spring-boot:run
    ```
   > The API is now running at [http://localhost:8080](http://localhost:8080)

---

## How to Run (with Docker)

1. **Build Docker image:**
    ```
    docker build -t dispatch-load-balancer .
    ```
2. **Run container:**
    ```
    docker run -p 8080:8080 dispatch-load-balancer
    ```
   > Access API at: [http://localhost:8080](http://localhost:8080)

---

## Testing & Postman

- Use the included Postman collection (`postman/Dispatch-API.postman_collection.json`) or build your own using above requests.
- Test for: valid, over-capacity, duplicate ID, missing fields, and edge cases.
- All tests (`mvn test`) must pass.

---

## Tech Stack

- **Java 17+**
- **Spring Boot 3.x**
- **Maven**
- **JUnit 5**
- **Docker**
- **H2 In-memory Database**

---

## Assumptions

- Orders/vehicles have unique IDs and are submitted before `/plan`.
- Invalid input or non-assignable orders are handled and validated.
- All coordinates valid and use decimal degrees.
- No UI; REST-only.

---

## Error Handling

- **400 Bad Request** for missing fields, invalid coordinates, negatives.
- **409 Conflict** for duplicate IDs.
- Graceful, descriptive validation for bad or edge-case input.

---

## Performance

- Efficient for hundreds of orders and vehicles (no O(N^2) code).
- Minimal DB operations (batch upserts).
- Prioritizes/assigns by priority, then minimizes travel distance.

---

## GitHub
---
1. **Check https://github.com/bhargavv1704/dispatch-load-balancer**

---

## Contact

For questions or clarifications, contact: manishbhargav1014@gmail.com

---
