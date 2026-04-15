package com.example.qrcrowdmanagement.repository;

import com.example.qrcrowdmanagement.entity.User;
import com.example.qrcrowdmanagement.model.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByApprovalStatusOrderByIdDesc(ApprovalStatus approvalStatus);
}

