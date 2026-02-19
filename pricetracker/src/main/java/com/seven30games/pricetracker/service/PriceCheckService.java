package com.seven30games.pricetracker.service;

import com.seven30games.pricetracker.model.PriceHistory;
import com.seven30games.pricetracker.model.Product;
import com.seven30games.pricetracker.repository.PriceHistoryRepository;
import com.seven30games.pricetracker.repository.ProductRepository;
import com.seven30games.pricetracker.scraper.PriceResult;
import com.seven30games.pricetracker.scraper.ScraperDispatcher;
import com.seven30games.pricetracker.scraper.ScrapingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PriceCheckService {

    private static final Logger log = LoggerFactory.getLogger(PriceCheckService.class);

    private final ProductRepository productRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final ScraperDispatcher scraperDispatcher;
    private final NotificationService notificationService;

    public PriceCheckService(ProductRepository productRepository,
                              PriceHistoryRepository priceHistoryRepository,
                              ScraperDispatcher scraperDispatcher,
                              NotificationService notificationService) {
        this.productRepository = productRepository;
        this.priceHistoryRepository = priceHistoryRepository;
        this.scraperDispatcher = scraperDispatcher;
        this.notificationService = notificationService;
    }

    /**
     * Called by the scheduler — checks all active products in batches.
     */
    public void checkAllActiveProducts() {
        List<Product> products = productRepository.findByActiveTrue();
        log.info("Starting scheduled price check for {} active products", products.size());

        for (Product product : products) {
            try {
                checkProduct(product);
                // Stagger requests to avoid hammering servers
                Thread.sleep(1000 + (long)(Math.random() * 2000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Price check interrupted");
                break;
            }
        }
    }

    /**
     * Check a single product by ID (manual trigger or scheduler).
     */
    @Transactional
    public void checkProductById(UUID productId) {
        productRepository.findById(productId).ifPresent(this::checkProduct);
    }

    @Transactional
    public void checkProduct(Product product) {
        log.info("Checking price for product {} ({})", product.getId(), product.getUrl());

        PriceResult result;
        try {
            result = scraperDispatcher.dispatch(product.getUrl());
        } catch (ScrapingException e) {
            log.warn("Scraping failed for product {}: {}", product.getId(), e.getMessage());
            return;
        }

        // Persist price history (append-only — never update)
        PriceHistory entry = new PriceHistory();
        entry.setProduct(product);
        entry.setPrice(result.price());
        entry.setCurrency(result.currency());
        entry.setInStock(result.inStock());
        priceHistoryRepository.save(entry);

        // Update product name from last scrape if not set
        // (We'd need name from scrape result — future improvement)

        // Check if price has dropped to or below target
        if (product.getTargetPrice() != null
                && result.price().compareTo(product.getTargetPrice()) <= 0) {

            Optional<PriceHistory> previousEntry = priceHistoryRepository
                .findTopByProductIdOrderByScrapedAtDesc(product.getId());

            boolean alreadyAtTarget = previousEntry.isPresent()
                && previousEntry.get().getPrice().compareTo(product.getTargetPrice()) <= 0
                && !previousEntry.get().getId().equals(entry.getId());

            // Only alert if this is a NEW drop below target (not already there)
            if (!alreadyAtTarget) {
                log.info("PRICE DROP detected for product {} — {} <= target {}",
                    product.getId(), result.price(), product.getTargetPrice());
                notificationService.sendPriceDropAlert(product, result.price());
            }
        }
    }
}
