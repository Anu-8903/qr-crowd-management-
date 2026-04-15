package com.example.qrcrowdmanagement.dto;

import com.example.qrcrowdmanagement.model.EntryStatus;
import com.example.qrcrowdmanagement.model.MovementType;

import java.time.LocalDateTime;

public class EntryHistoryItem {
    private Long entryId;
    private Long zoneId;
    private String zoneName;
    private LocalDateTime entryTime;
    private EntryStatus status;
    private MovementType movementType;

    public Long getEntryId() {
        return entryId;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public EntryStatus getStatus() {
        return status;
    }

    public void setStatus(EntryStatus status) {
        this.status = status;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }
}

