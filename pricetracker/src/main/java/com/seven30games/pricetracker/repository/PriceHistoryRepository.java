package com.seven30games.pricetracker.repository;

import com.seven30games.pricetracker.model.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, UUID> {

    List<PriceHistory> findByProductIdOrderByScrapedAtDesc(UUID productId);

    List<PriceHistory> findByProductIdAndScrapedAtAfterOrderByScrapedAtAsc(UUID productId, Instant since);

    Optional<PriceHistory> findTopByProductIdOrderByScrapedAtDesc(UUID productId);

    @Query("SELECT MIN(p.price) FROM PriceHistory p WHERE p.product.id = :productId")
    Optional<BigDecimal> findMinPriceByProductId(@Param("productId") UUID productId);

    @Query("SELECT MAX(p.price) FROM PriceHistory p WHERE p.product.id = :productId")
    Optional<BigDecimal> findMaxPriceByProductId(@Param("productId") UUID productId);

    @Query("SELECT AVG(p.price) FROM PriceHistory p WHERE p.product.id = :productId")
    Optional<BigDecimal> findAvgPriceByProductId(@Param("productId") UUID productId);
}
