package com.seven30games.pricetracker.repository;

import com.seven30games.pricetracker.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, UUID> {

    @Query("SELECT a FROM Alert a JOIN a.product p WHERE p.user.id = :userId ORDER BY a.triggeredAt DESC")
    List<Alert> findByUserId(@Param("userId") UUID userId);

    List<Alert> findByNotificationSentFalse();
}
