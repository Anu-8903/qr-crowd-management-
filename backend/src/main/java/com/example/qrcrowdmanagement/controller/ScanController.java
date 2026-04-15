package com.example.qrcrowdmanagement.controller;

import com.example.qrcrowdmanagement.dto.ScanQrRequest;
import com.example.qrcrowdmanagement.dto.ScanQrResponse;
import com.example.qrcrowdmanagement.service.EntryService;
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
public class ScanController {

    private final EntryService entryService;

    public ScanController(EntryService entryService) {
        this.entryService = entryService;
    }

    @PostMapping("/scan")
    public ResponseEntity<ScanQrResponse> scan(@Valid @RequestBody ScanQrRequest request) {
        return ResponseEntity.ok(entryService.scanQr(request));
    }
}

