package com.example.qrcrowdmanagement.repository;

import com.example.qrcrowdmanagement.entity.Alert;
import com.example.qrcrowdmanagement.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByZoneOrderByAlertTimeDesc(Zone zone);
    List<Alert> findTop5ByZoneOrderByAlertTimeDesc(Zone zone);
}

