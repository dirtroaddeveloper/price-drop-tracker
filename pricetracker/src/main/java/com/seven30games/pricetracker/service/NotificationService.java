package com.seven30games.pricetracker.service;

import com.seven30games.pricetracker.model.Alert;
import com.seven30games.pricetracker.model.Product;
import com.seven30games.pricetracker.repository.AlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender mailSender;
    private final AlertRepository alertRepository;

    public NotificationService(JavaMailSender mailSender, AlertRepository alertRepository) {
        this.mailSender = mailSender;
        this.alertRepository = alertRepository;
    }

    public void sendPriceDropAlert(Product product, BigDecimal currentPrice) {
        String recipientEmail = product.getUser().getNotificationEmail() != null
            ? product.getUser().getNotificationEmail()
            : product.getUser().getEmail();

        // Save alert record first
        Alert alert = new Alert();
        alert.setProduct(product);
        alert.setTriggeredPrice(currentPrice);
        alert.setAlertType("PRICE_DROP");

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipientEmail);
            message.setSubject("Price Drop Alert: " + (product.getName() != null ? product.getName() : "Your tracked item"));
            message.setText(buildEmailBody(product, currentPrice));
            mailSender.send(message);

            alert.setNotificationSent(true);
            log.info("Price drop email sent to {} for product {}", recipientEmail, product.getId());
        } catch (Exception e) {
            alert.setNotificationSent(false);
            log.error("Failed to send price drop email for product {}: {}", product.getId(), e.getMessage());
        }

        alertRepository.save(alert);
    }

    private String buildEmailBody(Product product, BigDecimal currentPrice) {
        return String.format("""
            Price Drop Alert!

            Product: %s
            Current Price: $%.2f
            Your Target Price: $%.2f

            Shop now: %s

            --
            Price Drop Tracker by Seven 30 Games
            """,
            product.getName() != null ? product.getName() : "Unknown Product",
            currentPrice,
            product.getTargetPrice(),
            product.getUrl()
        );
    }
}
