package com.example.qrcrowdmanagement.dto;

import jakarta.validation.constraints.NotNull;

public class ChangeUserZoneRequest {

    @NotNull
    private Long zoneId;

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }
}

