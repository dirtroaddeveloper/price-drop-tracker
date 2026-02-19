package com.seven30games.pricetracker.scraper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScraperDispatcher {

    private static final Logger log = LoggerFactory.getLogger(ScraperDispatcher.class);

    private final List<ScraperStrategy> strategies;
    private final GenericScraper genericScraper;

    public ScraperDispatcher(List<ScraperStrategy> strategies, GenericScraper genericScraper) {
        // Inject all strategies; remove generic from the specific list to control order
        this.strategies = strategies.stream()
            .filter(s -> !(s instanceof GenericScraper))
            .toList();
        this.genericScraper = genericScraper;
    }

    public PriceResult dispatch(String url) throws ScrapingException {
        for (ScraperStrategy strategy : strategies) {
            if (strategy.canHandle(url)) {
                log.info("Dispatching to {} for URL: {}", strategy.getClass().getSimpleName(), url);
                return strategy.scrape(url);
            }
        }
        // Fallback to generic
        log.info("No specific scraper matched, falling back to GenericScraper for URL: {}", url);
        return genericScraper.scrape(url);
    }
}
