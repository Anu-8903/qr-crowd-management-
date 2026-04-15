package com.example.qrcrowdmanagement.service;

import com.example.qrcrowdmanagement.dto.QrCodeResponse;
import com.example.qrcrowdmanagement.entity.QrCode;
import com.example.qrcrowdmanagement.entity.User;
import com.example.qrcrowdmanagement.entity.Zone;
import com.example.qrcrowdmanagement.repository.QrCodeRepository;
import com.example.qrcrowdmanagement.repository.ZoneRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class QrCodeService {

    private static final int QR_EXPIRY_MINUTES = 5;

    private final QrCodeRepository qrCodeRepository;
    private final ZoneRepository zoneRepository;
    private final QrTokenService qrTokenService;

    public QrCodeService(QrCodeRepository qrCodeRepository, ZoneRepository zoneRepository, QrTokenService qrTokenService) {
        this.qrCodeRepository = qrCodeRepository;
        this.zoneRepository = zoneRepository;
        this.qrTokenService = qrTokenService;
    }

    @Transactional
    public QrCodeResponse generateForZone(Long zoneId) {
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new IllegalArgumentException("Zone not found: " + zoneId));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusMinutes(QR_EXPIRY_MINUTES);

        String value = "ZONE:" + zone.getId() + ":" + UUID.randomUUID();

        QrCode qrCode = new QrCode();
        qrCode.setZone(zone);
        qrCode.setQrValue(value);
        qrCode.setGeneratedTime(now);
        qrCode.setExpiryTime(expiry);

        QrCode saved = qrCodeRepository.save(qrCode);

        String imageBase64 = QrCodeGenerator.generateQrPngBase64(value, 300, 300);

        QrCodeResponse response = new QrCodeResponse();
        response.setZoneId(zone.getId());
        response.setQrValue(saved.getQrValue());
        response.setQrImageBase64(imageBase64);
        response.setGeneratedTime(saved.getGeneratedTime());
        response.setExpiryTime(saved.getExpiryTime());
        return response;
    }


@Transactional
public QrCodeResponse generateForUser(User user) {

    Zone zone = user.getAllocatedZone();
    if (zone == null) {
        zone = zoneRepository.findAll().stream()
                .filter(z -> z.getCurrentCount() < z.getMaxCapacity())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("All zones are full"));
    }

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime expiry = now.plusMinutes(QR_EXPIRY_MINUTES);

    long ts = System.currentTimeMillis();
    String nonce = UUID.randomUUID().toString().replace("-", "");
    String payload = "UID=" + user.getId() + "&ZID=" + zone.getId() + "&TS=" + ts + "&N=" + nonce;
    String sig = qrTokenService.sign(payload);
    String value = payload + "&SIG=" + sig;

    QrCode qrCode = new QrCode();
    qrCode.setZone(zone);
    qrCode.setUser(user);
    qrCode.setQrValue(value);
    qrCode.setGeneratedTime(now);
    qrCode.setExpiryTime(expiry);

    QrCode saved = qrCodeRepository.save(qrCode);

    String imageBase64 = QrCodeGenerator.generateQrPngBase64(value, 300, 300);

    QrCodeResponse response = new QrCodeResponse();
    response.setZoneId(zone.getId());
    response.setQrValue(saved.getQrValue());
    response.setQrImageBase64(imageBase64);
    response.setGeneratedTime(saved.getGeneratedTime());
    response.setExpiryTime(saved.getExpiryTime());

    return response;
}



    @Transactional(readOnly = true)
    public QrCode validateActiveByValue(String qrValue) {
        QrCode qrCode = qrCodeRepository.findByQrValue(qrValue)
                .orElseThrow(() -> new IllegalArgumentException("Invalid QR code"));
        if (qrCode.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("QR code has expired");
        }
        return qrCode;
    }

    public Long extractUserId(String qrValue) {
        return Long.parseLong(getParam(qrValue, "UID"));
    }

    public Long extractZoneId(String qrValue) {
        return Long.parseLong(getParam(qrValue, "ZID"));
    }

    public void validateSignature(String qrValue) {
        String sig = getParam(qrValue, "SIG");
        String payload = qrValue.substring(0, qrValue.lastIndexOf("&SIG="));
        String expected = qrTokenService.sign(payload);
        if (!expected.equals(sig)) {
            throw new IllegalArgumentException("Invalid QR signature");
        }
    }

    private String getParam(String qrValue, String key) {
        String[] parts = qrValue.split("&");
        for (String p : parts) {
            String[] kv = p.split("=", 2);
            if (kv.length == 2 && kv[0].equals(key)) {
                return kv[1];
            }
        }
        throw new IllegalArgumentException("Invalid QR format");
    }

    /**
     * Periodic cleanup of expired QR codes. Runs every 5 minutes.
     */
    @Scheduled(fixedRate = 5 * 60 * 1000L)
    @Transactional
    public void cleanupExpiredQrCodes() {
        LocalDateTime now = LocalDateTime.now();
        qrCodeRepository.deleteByExpiryTimeBefore(now);
    }
}

