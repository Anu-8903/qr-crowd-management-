package com.example.qrcrowdmanagement.dto;

public class PendingUserItem {
    private Long userId;
    private String name;
    private String email;
    private Long zoneId;
    private String zoneName;
    private Integer zoneCapacity;
    private Integer zoneCurrentCount;
    private Integer zoneAvailable;

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

    public Integer getZoneCapacity() {
        return zoneCapacity;
    }

    public void setZoneCapacity(Integer zoneCapacity) {
        this.zoneCapacity = zoneCapacity;
    }

    public Integer getZoneCurrentCount() {
        return zoneCurrentCount;
    }

    public void setZoneCurrentCount(Integer zoneCurrentCount) {
        this.zoneCurrentCount = zoneCurrentCount;
    }

    public Integer getZoneAvailable() {
        return zoneAvailable;
    }

    public void setZoneAvailable(Integer zoneAvailable) {
        this.zoneAvailable = zoneAvailable;
    }
}

