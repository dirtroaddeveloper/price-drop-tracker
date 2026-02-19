package com.seven30games.pricetracker.dto;

import com.seven30games.pricetracker.model.Product;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
    UUID id,
    String url,
    String name,
    String retailer,
    BigDecimal targetPrice,
    boolean active,
    Instant createdAt
) {
    public static ProductResponse from(Product p) {
        return new ProductResponse(
            p.getId(), p.getUrl(), p.getName(), p.getRetailer(),
            p.getTargetPrice(), p.isActive(), p.getCreatedAt()
        );
    }
}
