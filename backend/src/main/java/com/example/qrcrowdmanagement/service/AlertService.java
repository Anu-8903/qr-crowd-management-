package com.example.qrcrowdmanagement.service;

import com.example.qrcrowdmanagement.dto.AlertDto;
import com.example.qrcrowdmanagement.entity.Alert;
import com.example.qrcrowdmanagement.entity.Zone;
import com.example.qrcrowdmanagement.repository.AlertRepository;
import com.example.qrcrowdmanagement.repository.ZoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final ZoneRepository zoneRepository;

    public AlertService(AlertRepository alertRepository, ZoneRepository zoneRepository) {
        this.alertRepository = alertRepository;
        this.zoneRepository = zoneRepository;
    }

   @Transactional
public Alert createAlert(Zone zone, String message, String type, String severity, Long userId) {
    Alert alert = new Alert();
    alert.setZone(zone);
    alert.setAlertMessage(message);
    alert.setAlertType(type);
    alert.setSeverity(severity);
    alert.setUserId(userId);
    alert.setResolved(false);
    alert.setAlertTime(LocalDateTime.now());

    return alertRepository.save(alert);
}

    

    @Transactional(readOnly = true)
    public List<AlertDto> getAllAlerts() {
        return alertRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AlertDto> getAlertsForZone(Long zoneId) {
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new IllegalArgumentException("Zone not found: " + zoneId));
        return alertRepository.findByZoneOrderByAlertTimeDesc(zone).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    
    private AlertDto toDto(Alert alert) {
        AlertDto dto = new AlertDto();
        dto.setId(alert.getId());
        dto.setZoneId(alert.getZone() != null ? alert.getZone().getId() : null);
        dto.setZoneName(alert.getZone() != null ? alert.getZone().getZoneName() : null);
        dto.setAlertMessage(alert.getAlertMessage());
        dto.setAlertTime(alert.getAlertTime());
        dto.setAlertType(alert.getAlertType());
        dto.setSeverity(alert.getSeverity());
        dto.setUserId(alert.getUserId());
        dto.setResolved(alert.isResolved());
        return dto;
    }
    public void checkOvercrowding(Zone zone) {
    int current = zone.getCurrentCount();
    int max = zone.getMaxCapacity();

    double percent = (current * 100.0) / max;

    if (percent >= 100) {
        createAlert(zone, "Zone capacity exceeded!", "OVERCROWD", "HIGH", null);
    } else if (percent >= 80) {
        createAlert(zone, "Zone nearing capacity", "OVERCROWD", "MEDIUM", null);
    }
}
public void invalidScan(String qrValue) {
    createAlert(
        null,
        "Invalid QR scanned: " + qrValue,
        "INVALID_SCAN",
        "HIGH",
        null
    );
}
public void suspiciousUser(Long userId, Zone zone) {
    createAlert(zone, "User scanning too fast", "SUSPICIOUS", "MEDIUM", userId);
}

public void unauthorizedAccess(Long userId, Zone scannedZone) {
    String zoneName = scannedZone != null ? scannedZone.getZoneName() : "UNKNOWN";
    createAlert(scannedZone, "Unauthorized access attempt by user " + userId + " at zone " + zoneName,
            "UNAUTHORIZED", "HIGH", userId);
}
}

