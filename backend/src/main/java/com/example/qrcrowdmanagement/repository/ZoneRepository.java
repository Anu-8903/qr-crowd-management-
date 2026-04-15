package com.example.qrcrowdmanagement.repository;

import com.example.qrcrowdmanagement.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ZoneRepository extends JpaRepository<Zone, Long> {

    @Query(value = """
            select * from zones
            where current_count < max_capacity
            order by current_count asc
            limit 1
            """, nativeQuery = true)
    Optional<Zone> findFirstAvailableLeastCrowded();
}

