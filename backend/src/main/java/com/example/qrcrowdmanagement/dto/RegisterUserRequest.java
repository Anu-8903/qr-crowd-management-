package com.example.qrcrowdmanagement.dto;

import com.example.qrcrowdmanagement.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RegisterUserRequest {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    private Role role;

    /**
     * Optional: user-selected zone. If null -> auto suggestion is used.
     */
    private Long zoneId;

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }
}

