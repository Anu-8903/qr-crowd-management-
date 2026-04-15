package com.example.qrcrowdmanagement.service;

import com.example.qrcrowdmanagement.entity.User;
import com.example.qrcrowdmanagement.entity.Zone;
import com.example.qrcrowdmanagement.model.ApprovalStatus;
import com.example.qrcrowdmanagement.model.Role;
import com.example.qrcrowdmanagement.model.UserStatus;
import com.example.qrcrowdmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ZoneService zoneService;

    public UserService(UserRepository userRepository, ZoneService zoneService) {
        this.userRepository = userRepository;
        this.zoneService = zoneService;
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    @Transactional
    public User registerUser(String name, String email, Role role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        Zone allocated = zoneService.allocateZoneOrThrow();

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setRole(role != null ? role : Role.USER);
        user.setAllocatedZone(allocated);
        user.setStatus(UserStatus.OUTSIDE);
        user.setApprovalStatus(ApprovalStatus.PENDING);
        return userRepository.save(user);
    }

    @Transactional
    public User registerUserWithZoneOption(String name, String email, Role role, Long preferredZoneId) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        Zone allocated;
        if (preferredZoneId != null) {
            allocated = zoneService.getZoneOrThrow(preferredZoneId);
            if (allocated.getCurrentCount() >= allocated.getMaxCapacity()) {
                throw new IllegalStateException("Selected zone is full");
            }
        } else {
            allocated = zoneService.allocateZoneOrThrow();
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setRole(role != null ? role : Role.USER);
        user.setAllocatedZone(allocated);
        user.setStatus(UserStatus.OUTSIDE);
        user.setApprovalStatus(ApprovalStatus.PENDING);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> getPendingUsers() {
        return userRepository.findByApprovalStatusOrderByIdDesc(ApprovalStatus.PENDING);
    }

    @Transactional
    public User changeUserZone(Long userId, Long zoneId) {
        User user = getById(userId);
        Zone zone = zoneService.getZoneOrThrow(zoneId);
        if (zone.getCurrentCount() >= zone.getMaxCapacity()) {
            throw new IllegalStateException("Selected zone is full");
        }
        user.setAllocatedZone(zone);
        return userRepository.save(user);
    }

    @Transactional
    public User approveUser(Long userId) {
        User user = getById(userId);
        if (user.getAllocatedZone() == null) {
            throw new IllegalStateException("User has no zone assigned");
        }
        user.setApprovalStatus(ApprovalStatus.APPROVED);
        return userRepository.save(user);
    }

    @Transactional
    public void rejectUser(Long userId) {
        User user = getById(userId);
        user.setApprovalStatus(ApprovalStatus.REJECTED);
        userRepository.save(user);
    }
}

