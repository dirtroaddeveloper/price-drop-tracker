package com.seven30games.pricetracker.scraper;

public interface ScraperStrategy {
    boolean canHandle(String url);
    PriceResult scrape(String url) throws ScrapingException;
}
