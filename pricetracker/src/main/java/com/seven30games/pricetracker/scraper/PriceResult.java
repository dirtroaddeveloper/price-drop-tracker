package com.seven30games.pricetracker.scraper;

import java.math.BigDecimal;

public record PriceResult(BigDecimal price, String currency, boolean inStock) {}
