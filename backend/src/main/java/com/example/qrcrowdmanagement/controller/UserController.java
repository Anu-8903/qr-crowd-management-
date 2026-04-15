package com.example.qrcrowdmanagement.controller;

import com.example.qrcrowdmanagement.dto.EntryHistoryItem;
import com.example.qrcrowdmanagement.dto.RegisterUserRequest;
import com.example.qrcrowdmanagement.dto.RegisterUserResponse;
import com.example.qrcrowdmanagement.dto.ScanQrRequest;
import com.example.qrcrowdmanagement.dto.ScanQrResponse;
import com.example.qrcrowdmanagement.dto.ZoneStatusResponse;
import com.example.qrcrowdmanagement.entity.User;
import com.example.qrcrowdmanagement.service.EntryService;
import com.example.qrcrowdmanagement.service.QrCodeService;
import com.example.qrcrowdmanagement.service.UserService;
import com.example.qrcrowdmanagement.service.ZoneService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    private final EntryService entryService;
    private final ZoneService zoneService;
    private final UserService userService;
    private final QrCodeService qrCodeService;

    public UserController(EntryService entryService,
                          ZoneService zoneService,
                          UserService userService,
                          QrCodeService qrCodeService) {
        this.entryService = entryService;
        this.zoneService = zoneService;
        this.userService = userService;
        this.qrCodeService = qrCodeService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        User user = userService.registerUserWithZoneOption(
                request.getName(),
                request.getEmail(),
                request.getRole(),
                request.getZoneId()
        );

        RegisterUserResponse resp = new RegisterUserResponse();
        resp.setUserId(user.getId());
        resp.setName(user.getName());
        resp.setEmail(user.getEmail());
        resp.setApprovalStatus(user.getApprovalStatus() != null ? user.getApprovalStatus().name() : null);
        resp.setQrGenerated(false);
        if (user.getAllocatedZone() != null) {
            resp.setAllocatedZoneId(user.getAllocatedZone().getId());
            resp.setAllocatedZoneName(user.getAllocatedZone().getZoneName());
            resp.setAllocatedZoneLocation(user.getAllocatedZone().getLocation());
            resp.setAllocatedZoneMaxCapacity(user.getAllocatedZone().getMaxCapacity());
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/scan")
    public ResponseEntity<ScanQrResponse> scanQRCode(@Valid @RequestBody ScanQrRequest request) {
        return ResponseEntity.ok(entryService.scanQr(request));
    }

    @GetMapping("/zones/{zoneId}/status")
    public ResponseEntity<ZoneStatusResponse> checkZoneStatus(@PathVariable Long zoneId) {
        return ResponseEntity.ok(zoneService.getZoneStatus(zoneId));
    }

    @GetMapping("/users/{userId}/entries")
    public ResponseEntity<List<EntryHistoryItem>> getEntryHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(entryService.getEntryHistoryForUser(userId));
    }
}

