package com.example.qrcrowdmanagement.service;

import com.example.qrcrowdmanagement.dto.CreateZoneRequest;
import com.example.qrcrowdmanagement.dto.ZoneDto;
import com.example.qrcrowdmanagement.dto.ZoneStatusResponse;
import com.example.qrcrowdmanagement.entity.Zone;
import com.example.qrcrowdmanagement.repository.ZoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ZoneService {

    private final ZoneRepository zoneRepository;
    private final AlertService alertService;

    public ZoneService(ZoneRepository zoneRepository, AlertService alertService) {
        this.zoneRepository = zoneRepository;
        this.alertService = alertService;
    }

    @Transactional
    public ZoneDto createZone(CreateZoneRequest request) {
        Zone zone = new Zone();
        zone.setZoneName(request.getZoneName());
        zone.setLocation(request.getLocation());
        zone.setMaxCapacity(request.getMaxCapacity());
        zone.setCurrentCount(0);
        zone.setCrowdLevel("LOW");
        Zone saved = zoneRepository.save(zone);
        return toDto(saved);
    }

    @Transactional
    public ZoneDto updateZoneCapacity(Long zoneId, int maxCapacity) {
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new IllegalArgumentException("Zone not found: " + zoneId));
        zone.setMaxCapacity(maxCapacity);
        if (zone.getCurrentCount() > maxCapacity) {
            zone.setCurrentCount(maxCapacity);
        }
        Zone saved = zoneRepository.save(zone);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public ZoneStatusResponse getZoneStatus(Long zoneId) {
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new IllegalArgumentException("Zone not found: " + zoneId));
        return toStatus(zone);
    }

    @Transactional(readOnly = true)
    public List<ZoneDto> getAllZones() {
        return zoneRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void incrementCurrentCount(Zone zone) {
        zone.setCurrentCount(zone.getCurrentCount() + 1);
        Zone saved = zoneRepository.save(zone);
        updateCrowdLevelIfNeeded(saved);
    }

    @Transactional
    public void decrementCurrentCount(Zone zone) {
        int next = zone.getCurrentCount() - 1;
        zone.setCurrentCount(Math.max(next, 0));
        Zone saved = zoneRepository.save(zone);
        updateCrowdLevelIfNeeded(saved);
    }

    @Transactional
    public void setCurrentCount(Zone zone, int newCount) {
        zone.setCurrentCount(newCount);
        Zone saved = zoneRepository.save(zone);
        updateCrowdLevelIfNeeded(saved);
    }
    
    @Transactional(readOnly = true)
public Zone allocateZone() {

    List<Zone> zones = zoneRepository.findAll();

    for (Zone z : zones) {
        if (z.getCurrentCount() < z.getMaxCapacity()) {
            return z;
        }
    }

    return null; // all full
}

    @Transactional(readOnly = true)
    public Zone allocateZoneOrThrow() {
        return zoneRepository.findFirstAvailableLeastCrowded()
                .orElseThrow(() -> new IllegalStateException("All zones are full"));
    }

    private ZoneDto toDto(Zone zone) {
        ZoneDto dto = new ZoneDto();
        dto.setId(zone.getId());
        dto.setZoneName(zone.getZoneName());
        dto.setLocation(zone.getLocation());
        dto.setMaxCapacity(zone.getMaxCapacity());
        dto.setCurrentCount(zone.getCurrentCount());
        dto.setCrowdLevel(zone.getCrowdLevel());
        return dto;
    }

    private ZoneStatusResponse toStatus(Zone zone) {
        double percentage = zone.getMaxCapacity() == 0
                ? 0
                : (zone.getCurrentCount() * 100.0) / zone.getMaxCapacity();
        ZoneStatusResponse status = new ZoneStatusResponse();
        status.setZoneId(zone.getId());
        status.setZoneName(zone.getZoneName());
        status.setLocation(zone.getLocation());
        status.setMaxCapacity(zone.getMaxCapacity());
        status.setCurrentCount(zone.getCurrentCount());
        status.setOccupancyPercentage(percentage);
        status.setAtOrAboveCapacity(zone.getCurrentCount() >= zone.getMaxCapacity());
        status.setCrowdLevel(zone.getCrowdLevel());
        return status;
    }

    private void updateCrowdLevelIfNeeded(Zone zone) {
        String previous = zone.getCrowdLevel();
        String next = computeCrowdLevel(zone);
        if (next.equals(previous)) {
            return;
        }
        zone.setCrowdLevel(next);
        zoneRepository.save(zone);
        alertService.createAlert(zone, "Crowd level is now " + next + " (" + zone.getCurrentCount() + "/" + zone.getMaxCapacity() + ")",
                "CROWD_LEVEL", next, null);
    }

    private String computeCrowdLevel(Zone zone) {
        if (zone.getMaxCapacity() == null || zone.getMaxCapacity() == 0) {
            return "LOW";
        }
        double pct = (zone.getCurrentCount() * 100.0) / zone.getMaxCapacity();
        if (pct >= 80.0) {
            return "HIGH";
        }
        if (pct >= 50.0) {
            return "MEDIUM";
        }
        return "LOW";
    }

    @Transactional(readOnly = true)
    public Zone getZoneOrThrow(Long zoneId) {
        return zoneRepository.findById(zoneId)
                .orElseThrow(() -> new IllegalArgumentException("Zone not found: " + zoneId));
    }
}

