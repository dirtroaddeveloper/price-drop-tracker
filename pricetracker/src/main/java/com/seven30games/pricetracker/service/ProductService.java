package com.seven30games.pricetracker.service;

import com.seven30games.pricetracker.dto.PriceHistoryResponse;
import com.seven30games.pricetracker.dto.ProductRequest;
import com.seven30games.pricetracker.dto.ProductResponse;
import com.seven30games.pricetracker.exception.ResourceNotFoundException;
import com.seven30games.pricetracker.model.Product;
import com.seven30games.pricetracker.model.User;
import com.seven30games.pricetracker.repository.PriceHistoryRepository;
import com.seven30games.pricetracker.repository.ProductRepository;
import com.seven30games.pricetracker.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository productRepository,
                          PriceHistoryRepository priceHistoryRepository,
                          UserRepository userRepository) {
        this.productRepository = productRepository;
        this.priceHistoryRepository = priceHistoryRepository;
        this.userRepository = userRepository;
    }

    public List<ProductResponse> getProductsForUser(String email) {
        User user = findUser(email);
        return productRepository.findByUserIdAndActiveTrue(user.getId())
            .stream().map(ProductResponse::from).toList();
    }

    @Transactional
    public ProductResponse addProduct(String email, ProductRequest request) {
        User user = findUser(email);

        // Detect retailer from URL
        String retailer = detectRetailer(request.url());

        Product product = new Product();
        product.setUser(user);
        product.setUrl(request.url());
        product.setRetailer(retailer);
        product.setTargetPrice(request.targetPrice());
        productRepository.save(product);

        return ProductResponse.from(product);
    }

    public ProductResponse getProduct(String email, UUID productId) {
        User user = findUser(email);
        Product product = productRepository.findByIdAndUserId(productId, user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse updateProduct(String email, UUID productId, ProductRequest request) {
        User user = findUser(email);
        Product product = productRepository.findByIdAndUserId(productId, user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        product.setTargetPrice(request.targetPrice());
        if (request.url() != null && !request.url().isBlank()) {
            product.setUrl(request.url());
            product.setRetailer(detectRetailer(request.url()));
        }
        productRepository.save(product);
        return ProductResponse.from(product);
    }

    @Transactional
    public void deleteProduct(String email, UUID productId) {
        User user = findUser(email);
        Product product = productRepository.findByIdAndUserId(productId, user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        product.setActive(false);
        productRepository.save(product);
    }

    public List<PriceHistoryResponse> getPriceHistory(String email, UUID productId, Integer days) {
        User user = findUser(email);
        productRepository.findByIdAndUserId(productId, user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        if (days != null) {
            Instant since = Instant.now().minus(days, ChronoUnit.DAYS);
            return priceHistoryRepository
                .findByProductIdAndScrapedAtAfterOrderByScrapedAtAsc(productId, since)
                .stream().map(PriceHistoryResponse::from).toList();
        }

        return priceHistoryRepository.findByProductIdOrderByScrapedAtDesc(productId)
            .stream().map(PriceHistoryResponse::from).toList();
    }

    public Map<String, Object> getStats(String email, UUID productId) {
        User user = findUser(email);
        productRepository.findByIdAndUserId(productId, user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        BigDecimal min = priceHistoryRepository.findMinPriceByProductId(productId).orElse(null);
        BigDecimal max = priceHistoryRepository.findMaxPriceByProductId(productId).orElse(null);
        BigDecimal avg = priceHistoryRepository.findAvgPriceByProductId(productId).orElse(null);

        return Map.of(
            "lowestPrice", min != null ? min : "N/A",
            "highestPrice", max != null ? max : "N/A",
            "averagePrice", avg != null ? avg : "N/A"
        );
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    private String detectRetailer(String url) {
        if (url.contains("amazon.")) return "Amazon";
        if (url.contains("bestbuy.")) return "Best Buy";
        if (url.contains("walmart.")) return "Walmart";
        if (url.contains("newegg.")) return "Newegg";
        if (url.contains("target.")) return "Target";
        return "Other";
    }
}
