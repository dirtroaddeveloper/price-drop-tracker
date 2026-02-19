package com.seven30games.pricetracker.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Component
public class AmazonScraper implements ScraperStrategy {

    private static final Logger log = LoggerFactory.getLogger(AmazonScraper.class);

    private static final List<String> USER_AGENTS = List.of(
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    );

    private final Random random = new Random();

    @Override
    public boolean canHandle(String url) {
        return url != null && (url.contains("amazon.com") || url.contains("amazon.co.uk")
                || url.contains("amazon.ca") || url.contains("amazon.de"));
    }

    @Override
    public PriceResult scrape(String url) throws ScrapingException {
        try {
            // Random delay to avoid bot detection
            Thread.sleep(500 + random.nextInt(2500));

            Document doc = Jsoup.connect(url)
                .userAgent(USER_AGENTS.get(random.nextInt(USER_AGENTS.size())))
                .referrer("https://www.google.com")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "en-US,en;q=0.5")
                .timeout(10_000)
                .get();

            // Try multiple price selectors — Amazon changes these occasionally
            String[] priceSelectors = {
                "#corePriceDisplay_desktop_feature_div .a-price-whole",
                "#price_inside_buybox",
                "#priceblock_ourprice",
                "#priceblock_dealprice",
                ".a-price.aok-align-center .a-offscreen"
            };

            String rawPrice = null;
            for (String selector : priceSelectors) {
                Element el = doc.selectFirst(selector);
                if (el != null && !el.text().isBlank()) {
                    rawPrice = el.text();
                    break;
                }
            }

            if (rawPrice == null) {
                throw new ScrapingException("Could not locate price element on Amazon page: " + url);
            }

            BigDecimal price = parsePrice(rawPrice);

            // Check stock
            boolean inStock = doc.selectFirst("#availability .a-color-state") == null
                || !doc.selectFirst("#availability").text().toLowerCase().contains("unavailable");

            log.info("Amazon scrape success: url={} price={}", url, price);
            return new PriceResult(price, "USD", inStock);

        } catch (ScrapingException e) {
            throw e;
        } catch (IOException e) {
            throw new ScrapingException("Network error scraping Amazon URL: " + url, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ScrapingException("Scrape interrupted for URL: " + url, e);
        }
    }

    private BigDecimal parsePrice(String raw) throws ScrapingException {
        // Strip currency symbols, commas, whitespace — keep digits and decimal point
        String cleaned = raw.replaceAll("[^0-9.]", "").trim();
        if (cleaned.isEmpty()) {
            throw new ScrapingException("Could not parse price from text: " + raw);
        }
        try {
            return new BigDecimal(cleaned);
        } catch (NumberFormatException e) {
            throw new ScrapingException("Invalid price format: " + cleaned);
        }
    }
}
