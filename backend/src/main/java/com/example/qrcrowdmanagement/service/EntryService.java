package com.example.qrcrowdmanagement.service;

import com.example.qrcrowdmanagement.dto.EntryHistoryItem;
import com.example.qrcrowdmanagement.dto.ScanQrRequest;
import com.example.qrcrowdmanagement.dto.ScanQrResponse;
import com.example.qrcrowdmanagement.entity.Entry;
import com.example.qrcrowdmanagement.entity.QrCode;
import com.example.qrcrowdmanagement.entity.User;
import com.example.qrcrowdmanagement.entity.Zone;
import com.example.qrcrowdmanagement.model.EntryStatus;
import com.example.qrcrowdmanagement.model.MovementType;
import com.example.qrcrowdmanagement.model.ApprovalStatus;
import com.example.qrcrowdmanagement.model.UserStatus;
import com.example.qrcrowdmanagement.repository.EntryRepository;
import com.example.qrcrowdmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntryService {

    private final EntryRepository entryRepository;
    private final UserService userService;
    private final QrCodeService qrCodeService;
    private final ZoneService zoneService;
    private final AlertService alertService;
    private final UserRepository userRepository;

    public EntryService(EntryRepository entryRepository,
                        UserService userService,
                        QrCodeService qrCodeService,
                        ZoneService zoneService,
                        AlertService alertService,
                        UserRepository userRepository) {
        this.entryRepository = entryRepository;
        this.userService = userService;
        this.qrCodeService = qrCodeService;
        this.zoneService = zoneService;
        this.alertService = alertService;
        this.userRepository = userRepository;
    }

    /**
     * Handles QR scan by user: validates QR, checks capacity, persists entry and updates counts/alerts.
     */
    @Transactional
    public ScanQrResponse scanQr(ScanQrRequest request) {
        User user = userService.getById(request.getUserId());
        if (user.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new IllegalStateException("User not approved for entry");
        }
        qrCodeService.validateSignature(request.getQrValue());
        QrCode qrCode = qrCodeService.validateActiveByValue(request.getQrValue());

        Long qrUserId = qrCodeService.extractUserId(request.getQrValue());
        Long qrZoneId = qrCodeService.extractZoneId(request.getQrValue());

        if (!user.getId().equals(qrUserId)) {
            throw new IllegalArgumentException("User ID does not match QR");
        }

        Zone zone = qrCode.getZone();
        if (!zone.getId().equals(qrZoneId)) {
            throw new IllegalArgumentException("QR zone mismatch");
        }

        boolean authorized = user.getAllocatedZone() != null
                && user.getAllocatedZone().getId() != null
                && user.getAllocatedZone().getId().equals(zone.getId());

        MovementType movementType = MovementType.ENTRY;
        boolean allowed = false;
        String responseMessage;

        if (!authorized) {
            EntryStatus status = EntryStatus.DENIED;
            responseMessage = "Unauthorized access: user not allocated to this hall";
            alertService.unauthorizedAccess(user.getId(), zone);

            Entry entry = new Entry();
            entry.setUser(user);
            entry.setZone(zone);
            entry.setEntryTime(LocalDateTime.now());
            entry.setMovementType(MovementType.ENTRY);
            entry.setStatus(status);
            entryRepository.save(entry);

            ScanQrResponse resp = new ScanQrResponse();
            resp.setAuthorized(false);
            resp.setStatus(status);
            resp.setMessage(responseMessage);
            resp.setZoneId(zone.getId());
            resp.setZoneName(zone.getZoneName());
            resp.setCurrentCount(zone.getCurrentCount());
            resp.setMaxCapacity(zone.getMaxCapacity());
            resp.setMovementType(MovementType.ENTRY);
            return resp;
        }

        if (user.getStatus() == UserStatus.INSIDE) {
            // EXIT scan
            movementType = MovementType.EXIT;
            allowed = true;
            zoneService.decrementCurrentCount(zone);
            responseMessage = "Exit allowed";
            user.setStatus(UserStatus.OUTSIDE);
        } else {
            // ENTRY scan
            movementType = MovementType.ENTRY;
            allowed = zone.getCurrentCount() < zone.getMaxCapacity();
            if (allowed) {
                zoneService.incrementCurrentCount(zone);
                responseMessage = "Entry allowed";
                user.setStatus(UserStatus.INSIDE);
            } else {
                responseMessage = "Entry denied: capacity reached";
                String message = "Zone " + zone.getZoneName() + " has reached max capacity (" +
                        zone.getCurrentCount() + "/" + zone.getMaxCapacity() + ")";
                alertService.createAlert(zone, message, "OVERCROWD", "HIGH", user.getId());
            }
        }

        EntryStatus status = allowed ? EntryStatus.ALLOWED : EntryStatus.DENIED;

        Entry entry = new Entry();
        entry.setUser(user);
        entry.setZone(zone);
        entry.setEntryTime(LocalDateTime.now());
        entry.setMovementType(movementType);
        entry.setStatus(status);
        entryRepository.save(entry);
        userRepository.save(user);

        ScanQrResponse resp = new ScanQrResponse();
        resp.setAuthorized(true);
        resp.setStatus(status);
        resp.setMessage(responseMessage);
        resp.setZoneId(zone.getId());
        resp.setZoneName(zone.getZoneName());
        resp.setCurrentCount(zone.getCurrentCount());
        resp.setMaxCapacity(zone.getMaxCapacity());
        resp.setMovementType(movementType);
        return resp;
    }

    @Transactional(readOnly = true)
    public List<EntryHistoryItem> getEntryHistoryForUser(Long userId) {
        User user = userService.getById(userId);
        return entryRepository.findByUserOrderByEntryTimeDesc(user).stream()
                .map(this::toHistoryItem)
                .collect(Collectors.toList());
    }

    private EntryHistoryItem toHistoryItem(Entry entry) {
        EntryHistoryItem item = new EntryHistoryItem();
        item.setEntryId(entry.getId());
        item.setZoneId(entry.getZone().getId());
        item.setZoneName(entry.getZone().getZoneName());
        item.setEntryTime(entry.getEntryTime());
        item.setStatus(entry.getStatus());
        item.setMovementType(entry.getMovementType());
        return item;
    }
}

