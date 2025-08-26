package com.loadbalancer.dispatch.repository;

import com.loadbalancer.dispatch.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, String> {
}
