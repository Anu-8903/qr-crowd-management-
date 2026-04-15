package com.example.qrcrowdmanagement.repository;

import com.example.qrcrowdmanagement.entity.Entry;
import com.example.qrcrowdmanagement.entity.User;
import com.example.qrcrowdmanagement.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EntryRepository extends JpaRepository<Entry, Long> {

    List<Entry> findByUserOrderByEntryTimeDesc(User user);

    Optional<Entry> findTopByUserAndZoneOrderByEntryTimeDesc(User user, Zone zone);

    long countByZone(Zone zone);
}

