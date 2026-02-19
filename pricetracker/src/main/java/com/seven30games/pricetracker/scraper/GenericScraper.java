package com.seven30games.pricetracker.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GenericScraper implements ScraperStrategy {

    private static final Logger log = LoggerFactory.getLogger(GenericScraper.class);

    // Common price CSS classes / microdata patterns
    private static final String[] PRICE_SELECTORS = {
        "[itemprop='price']",
        ".price",
        ".product-price",
        ".sale-price",
        ".current-price",
        "[class*='price']"
    };

    private static final Pattern PRICE_PATTERN = Pattern.compile("\\$?([0-9]{1,3}(?:,[0-9]{3})*(?:\\.[0-9]{2})?)");

    @Override
    public boolean canHandle(String url) {
        return true; // Fallback — handles anything
    }

    @Override
    public PriceResult scrape(String url) throws ScrapingException {
        try {
            Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .referrer("https://www.google.com")
                .timeout(10_000)
                .get();

            for (String selector : PRICE_SELECTORS) {
                Element el = doc.selectFirst(selector);
                if (el != null) {
                    String text = el.hasAttr("content") ? el.attr("content") : el.text();
                    BigDecimal price = extractPrice(text);
                    if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
                        log.info("Generic scrape success via selector '{}': url={} price={}", selector, url, price);
                        return new PriceResult(price, "USD", true);
                    }
                }
            }

            throw new ScrapingException("Could not locate price on page (generic scraper): " + url);

        } catch (ScrapingException e) {
            throw e;
        } catch (IOException e) {
            throw new ScrapingException("Network error during generic scrape: " + url, e);
        }
    }

    private BigDecimal extractPrice(String text) {
        if (text == null || text.isBlank()) return null;
        Matcher m = PRICE_PATTERN.matcher(text);
        if (m.find()) {
            try {
                return new BigDecimal(m.group(1).replace(",", ""));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
