package com.loadbalancer.dispatch.repository;

import com.loadbalancer.dispatch.entity.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<VehicleEntity, String> {
}
