package com.example.qrcrowdmanagement.dto;

public class RegisterUserResponse {

    private Long userId;
    private String name;
    private String email;

    private Long allocatedZoneId;
    private String allocatedZoneName;
    private String allocatedZoneLocation;
    private Integer allocatedZoneMaxCapacity;

    private String approvalStatus;
    private boolean qrGenerated;
    private String userQrValue;
    private String userQrImageBase64;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getAllocatedZoneId() {
        return allocatedZoneId;
    }

    public void setAllocatedZoneId(Long allocatedZoneId) {
        this.allocatedZoneId = allocatedZoneId;
    }

    public String getAllocatedZoneName() {
        return allocatedZoneName;
    }

    public void setAllocatedZoneName(String allocatedZoneName) {
        this.allocatedZoneName = allocatedZoneName;
    }

    public String getAllocatedZoneLocation() {
        return allocatedZoneLocation;
    }

    public void setAllocatedZoneLocation(String allocatedZoneLocation) {
        this.allocatedZoneLocation = allocatedZoneLocation;
    }

    public Integer getAllocatedZoneMaxCapacity() {
        return allocatedZoneMaxCapacity;
    }

    public void setAllocatedZoneMaxCapacity(Integer allocatedZoneMaxCapacity) {
        this.allocatedZoneMaxCapacity = allocatedZoneMaxCapacity;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public boolean isQrGenerated() {
        return qrGenerated;
    }

    public void setQrGenerated(boolean qrGenerated) {
        this.qrGenerated = qrGenerated;
    }

    public String getUserQrValue() {
        return userQrValue;
    }

    public void setUserQrValue(String userQrValue) {
        this.userQrValue = userQrValue;
    }

    public String getUserQrImageBase64() {
        return userQrImageBase64;
    }

    public void setUserQrImageBase64(String userQrImageBase64) {
        this.userQrImageBase64 = userQrImageBase64;
    }
}

