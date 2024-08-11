package com.example.mapper;

import com.example.entity.VehicleLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleLogRepository extends JpaRepository<VehicleLog, Long> {
    VehicleLog findByLicensePlateAndExitTimeIsNull(String licensePlate);
}