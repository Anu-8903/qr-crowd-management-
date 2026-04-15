package com.example.qrcrowdmanagement.repository;

import com.example.qrcrowdmanagement.entity.QrCode;
import com.example.qrcrowdmanagement.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface QrCodeRepository extends JpaRepository<QrCode, Long> {

    Optional<QrCode> findTopByZoneOrderByGeneratedTimeDesc(Zone zone);

    Optional<QrCode> findByQrValue(String qrValue);

    long deleteByExpiryTimeBefore(LocalDateTime time);
}

