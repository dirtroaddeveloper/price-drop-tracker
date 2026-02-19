package com.seven30games.pricetracker.repository;

import com.seven30games.pricetracker.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByUserIdAndActiveTrue(UUID userId);
    List<Product> findByActiveTrue();
    Optional<Product> findByIdAndUserId(UUID id, UUID userId);
}
