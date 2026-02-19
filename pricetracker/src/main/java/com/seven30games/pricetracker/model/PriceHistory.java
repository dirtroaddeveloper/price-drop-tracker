package com.seven30games.pricetracker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "price_history")
@Getter @Setter @NoArgsConstructor
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(length = 3)
    private String currency = "USD";

    @Column(name = "scraped_at", nullable = false)
    private Instant scrapedAt = Instant.now();

    @Column(name = "in_stock", nullable = false)
    private boolean inStock = true;

    @Column(name = "raw_html_hash", length = 64)
    private String rawHtmlHash;
}
