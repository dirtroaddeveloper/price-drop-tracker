package com.seven30games.pricetracker.controller;

import com.seven30games.pricetracker.exception.ResourceNotFoundException;
import com.seven30games.pricetracker.model.Alert;
import com.seven30games.pricetracker.model.User;
import com.seven30games.pricetracker.repository.AlertRepository;
import com.seven30games.pricetracker.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;

    public AlertController(AlertRepository alertRepository, UserRepository userRepository) {
        this.alertRepository = alertRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<Alert>> list(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return ResponseEntity.ok(alertRepository.findByUserId(user.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> dismiss(@PathVariable UUID id) {
        Alert alert = alertRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Alert not found: " + id));
        alertRepository.delete(alert);
        return ResponseEntity.noContent().build();
    }
}
