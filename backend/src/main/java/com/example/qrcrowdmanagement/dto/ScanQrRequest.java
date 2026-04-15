package com.example.qrcrowdmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ScanQrRequest {

    @NotNull
    private Long userId;

    /**
     * Raw QR value scanned by the user.
     */
    @NotBlank
    private String qrValue;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getQrValue() {
        return qrValue;
    }

    public void setQrValue(String qrValue) {
        this.qrValue = qrValue;
    }
}

