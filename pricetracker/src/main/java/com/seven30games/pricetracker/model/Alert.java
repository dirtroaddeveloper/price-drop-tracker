package com.seven30games.pricetracker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "alerts")
@Getter @Setter @NoArgsConstructor
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "triggered_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal triggeredPrice;

    @Column(name = "triggered_at", nullable = false)
    private Instant triggeredAt = Instant.now();

    @Column(name = "notification_sent", nullable = false)
    private boolean notificationSent = false;

    @Column(name = "alert_type", length = 50)
    private String alertType = "PRICE_DROP";
}
