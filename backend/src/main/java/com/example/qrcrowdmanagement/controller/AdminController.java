package com.example.qrcrowdmanagement.controller;

import com.example.qrcrowdmanagement.dto.AlertDto;
import com.example.qrcrowdmanagement.dto.CreateZoneRequest;
import com.example.qrcrowdmanagement.dto.ChangeUserZoneRequest;
import com.example.qrcrowdmanagement.dto.EntryLogItem;
import com.example.qrcrowdmanagement.dto.ApproveUserResponse;
import com.example.qrcrowdmanagement.dto.PendingUserItem;
import com.example.qrcrowdmanagement.dto.QrCodeResponse;
import com.example.qrcrowdmanagement.dto.UpdateZoneCapacityRequest;
import com.example.qrcrowdmanagement.dto.ZoneDto;
import com.example.qrcrowdmanagement.dto.ZoneStatusResponse;
import com.example.qrcrowdmanagement.entity.Entry;
import com.example.qrcrowdmanagement.entity.User;
import com.example.qrcrowdmanagement.service.AlertService;
import com.example.qrcrowdmanagement.service.QrCodeService;
import com.example.qrcrowdmanagement.service.UserService;
import com.example.qrcrowdmanagement.service.ZoneService;
import com.example.qrcrowdmanagement.repository.EntryRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    private final ZoneService zoneService;
    private final QrCodeService qrCodeService;
    private final AlertService alertService;
    private final EntryRepository entryRepository;
    private final UserService userService;

    public AdminController(ZoneService zoneService,
                           QrCodeService qrCodeService,
                           AlertService alertService,
                           EntryRepository entryRepository,
                           UserService userService) {
        this.zoneService = zoneService;
        this.qrCodeService = qrCodeService;
        this.alertService = alertService;
        this.entryRepository = entryRepository;
        this.userService = userService;
    }

    @PostMapping("/zones")
    public ResponseEntity<ZoneDto> createZone(@Valid @RequestBody CreateZoneRequest request) {
        return ResponseEntity.ok(zoneService.createZone(request));
    }

    @PutMapping("/zones/{zoneId}/capacity")
    public ResponseEntity<ZoneDto> updateZoneCapacity(@PathVariable Long zoneId,
                                                      @Valid @RequestBody UpdateZoneCapacityRequest request) {
        return ResponseEntity.ok(zoneService.updateZoneCapacity(zoneId, request.getMaxCapacity()));
    }

    @PostMapping("/zones/{zoneId}/qrcode")
    public ResponseEntity<QrCodeResponse> generateQRCode(@PathVariable Long zoneId) {
        return ResponseEntity.ok(qrCodeService.generateForZone(zoneId));
    }
    @GetMapping("/zones/{zoneId}/status")
    public ResponseEntity<ZoneStatusResponse> getZoneCrowdStatus(@PathVariable Long zoneId) {
        return ResponseEntity.ok(zoneService.getZoneStatus(zoneId));
    }

    @GetMapping("/zones")
    public ResponseEntity<List<ZoneDto>> getAllZones() {
        return ResponseEntity.ok(zoneService.getAllZones());
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<AlertDto>> viewAlerts(@RequestParam(required = false) Long zoneId) {
        if (zoneId != null) {
            return ResponseEntity.ok(alertService.getAlertsForZone(zoneId));
        }
        return ResponseEntity.ok(alertService.getAllAlerts());
    }

    @GetMapping("/entries")
    public ResponseEntity<List<EntryLogItem>> viewEntryExitLogs() {
        List<Entry> all = entryRepository.findAll();
        List<EntryLogItem> items = all.stream()
                .sorted((a, b) -> b.getEntryTime().compareTo(a.getEntryTime()))
                .map(this::toLogItem)
                .collect(Collectors.toList());
        return ResponseEntity.ok(items);
    }

    @GetMapping("/users/pending")
    public ResponseEntity<List<PendingUserItem>> getPendingUsers() {
        List<User> pending = userService.getPendingUsers();
        List<PendingUserItem> items = pending.stream().map(this::toPendingItem).collect(Collectors.toList());
        return ResponseEntity.ok(items);
    }

    @PutMapping("/users/{userId}/zone")
    public ResponseEntity<PendingUserItem> changeUserZone(@PathVariable Long userId,
                                                          @Valid @RequestBody ChangeUserZoneRequest request) {
        User user = userService.changeUserZone(userId, request.getZoneId());
        return ResponseEntity.ok(toPendingItem(user));
    }

    @PostMapping("/users/{userId}/approve")
    public ResponseEntity<ApproveUserResponse> approve(@PathVariable Long userId) {
        User user = userService.approveUser(userId);
        var qr = qrCodeService.generateForUser(user);
        ApproveUserResponse resp = new ApproveUserResponse();
        resp.setUserId(user.getId());
        resp.setZoneId(user.getAllocatedZone() != null ? user.getAllocatedZone().getId() : null);
        resp.setApprovalStatus(user.getApprovalStatus() != null ? user.getApprovalStatus().name() : null);
        resp.setQrValue(qr.getQrValue());
        resp.setQrImageBase64(qr.getQrImageBase64());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/users/{userId}/reject")
    public ResponseEntity<?> reject(@PathVariable Long userId) {
        userService.rejectUser(userId);
        return ResponseEntity.ok().build();
    }

    private EntryLogItem toLogItem(Entry entry) {
        EntryLogItem item = new EntryLogItem();
        item.setId(entry.getId());
        item.setUserId(entry.getUser().getId());
        item.setUserName(entry.getUser().getName());
        item.setZoneId(entry.getZone().getId());
        item.setZoneName(entry.getZone().getZoneName());
        item.setMovementType(entry.getMovementType());
        var st = entry.getStatus();
        if (st == com.example.qrcrowdmanagement.model.EntryStatus.ENTERED
                || st == com.example.qrcrowdmanagement.model.EntryStatus.EXITED) {
            st = com.example.qrcrowdmanagement.model.EntryStatus.ALLOWED;
        }
        item.setStatus(st);
        item.setTime(entry.getEntryTime());
        return item;
    }

    private PendingUserItem toPendingItem(User user) {
        PendingUserItem item = new PendingUserItem();
        item.setUserId(user.getId());
        item.setName(user.getName());
        item.setEmail(user.getEmail());
        if (user.getAllocatedZone() != null) {
            item.setZoneId(user.getAllocatedZone().getId());
            item.setZoneName(user.getAllocatedZone().getZoneName());
            item.setZoneCapacity(user.getAllocatedZone().getMaxCapacity());
            item.setZoneCurrentCount(user.getAllocatedZone().getCurrentCount());
            item.setZoneAvailable(user.getAllocatedZone().getMaxCapacity() - user.getAllocatedZone().getCurrentCount());
        }
        return item;
    }
}

