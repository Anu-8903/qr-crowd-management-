package com.example.qrcrowdmanagement.dto;

public class ZoneStatusResponse {

    private Long zoneId;
    private String zoneName;
    private String location;
    private Integer maxCapacity;
    private Integer currentCount;
    private double occupancyPercentage;
    private boolean atOrAboveCapacity;
    private String crowdLevel;

    public ZoneStatusResponse() {
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Integer getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(Integer currentCount) {
        this.currentCount = currentCount;
    }

    public double getOccupancyPercentage() {
        return occupancyPercentage;
    }

    public void setOccupancyPercentage(double occupancyPercentage) {
        this.occupancyPercentage = occupancyPercentage;
    }

    public boolean isAtOrAboveCapacity() {
        return atOrAboveCapacity;
    }

    public void setAtOrAboveCapacity(boolean atOrAboveCapacity) {
        this.atOrAboveCapacity = atOrAboveCapacity;
    }

    public String getCrowdLevel() {
        return crowdLevel;
    }

    public void setCrowdLevel(String crowdLevel) {
        this.crowdLevel = crowdLevel;
    }
}

