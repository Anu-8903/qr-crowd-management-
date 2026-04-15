package com.example.qrcrowdmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = true)
    private Zone zone;

    @Column(name = "alert_message", nullable = false, length = 500)
    private String alertMessage;

    @Column(name = "alert_type")
    private String alertType;

    @Column(name = "severity")
    private String severity;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "resolved")
    private boolean resolved;

    @Column(name = "alert_time", nullable = false)
    private LocalDateTime alertTime;


    public Alert() {
    }

  public Alert(Zone zone, String message, String type, String severity, Long userId) {
    this.zone = zone;
    this.alertMessage = message;
    this.alertType = type;
    this.severity = severity;
    this.userId = userId;
    this.resolved = false;
    this.alertTime = LocalDateTime.now();
}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public String getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }

    public LocalDateTime getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(LocalDateTime alertTime) {
        this.alertTime = alertTime;
    }

    public String getAlertType() { return alertType; }
public void setAlertType(String alertType) { this.alertType = alertType; }

public String getSeverity() { return severity; }
public void setSeverity(String severity) { this.severity = severity; }

public Long getUserId() { return userId; }
public void setUserId(Long userId) { this.userId = userId; }

public boolean isResolved() { return resolved; }
public void setResolved(boolean resolved) { this.resolved = resolved; }
}

