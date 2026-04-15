package com.example.qrcrowdmanagement.controller;

import com.example.qrcrowdmanagement.dto.CreateZoneRequest;
import com.example.qrcrowdmanagement.dto.ZoneDto;
import com.example.qrcrowdmanagement.service.ZoneService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping
public class ZoneController {

    private final ZoneService zoneService;

    public ZoneController(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @PostMapping("/zones")
    public ResponseEntity<ZoneDto> create(@Valid @RequestBody CreateZoneRequest request) {
        return ResponseEntity.ok(zoneService.createZone(request));
    }
}

