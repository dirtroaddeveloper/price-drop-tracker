package com.seven30games.pricetracker.controller;

import com.seven30games.pricetracker.dto.PriceHistoryResponse;
import com.seven30games.pricetracker.dto.ProductRequest;
import com.seven30games.pricetracker.dto.ProductResponse;
import com.seven30games.pricetracker.service.PriceCheckService;
import com.seven30games.pricetracker.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final PriceCheckService priceCheckService;

    public ProductController(ProductService productService, PriceCheckService priceCheckService) {
        this.productService = productService;
        this.priceCheckService = priceCheckService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> list(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(productService.getProductsForUser(user.getUsername()));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@AuthenticationPrincipal UserDetails user,
                                                   @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(201).body(productService.addProduct(user.getUsername(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> get(@AuthenticationPrincipal UserDetails user,
                                                @PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProduct(user.getUsername(), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@AuthenticationPrincipal UserDetails user,
                                                   @PathVariable UUID id,
                                                   @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(user.getUsername(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserDetails user,
                                        @PathVariable UUID id) {
        productService.deleteProduct(user.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/check")
    public ResponseEntity<Map<String, String>> checkNow(@PathVariable UUID id) {
        priceCheckService.checkProductById(id);
        return ResponseEntity.ok(Map.of("status", "Price check triggered for product " + id));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<PriceHistoryResponse>> history(@AuthenticationPrincipal UserDetails user,
                                                               @PathVariable UUID id,
                                                               @RequestParam(required = false) Integer days) {
        return ResponseEntity.ok(productService.getPriceHistory(user.getUsername(), id, days));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> stats(@AuthenticationPrincipal UserDetails user,
                                                      @PathVariable UUID id) {
        return ResponseEntity.ok(productService.getStats(user.getUsername(), id));
    }
}
