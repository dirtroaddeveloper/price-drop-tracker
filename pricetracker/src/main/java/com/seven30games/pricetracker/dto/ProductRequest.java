package com.seven30games.pricetracker.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;

public record ProductRequest(
    @NotBlank @URL String url,
    @DecimalMin(value = "0.01", message = "Target price must be positive") BigDecimal targetPrice
) {}
