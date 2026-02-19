package com.seven30games.pricetracker.dto;

import com.seven30games.pricetracker.model.PriceHistory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PriceHistoryResponse(
    UUID id,
    BigDecimal price,
    String currency,
    Instant scrapedAt,
    boolean inStock
) {
    public static PriceHistoryResponse from(PriceHistory h) {
        return new PriceHistoryResponse(
            h.getId(), h.getPrice(), h.getCurrency(), h.getScrapedAt(), h.isInStock()
        );
    }
}
