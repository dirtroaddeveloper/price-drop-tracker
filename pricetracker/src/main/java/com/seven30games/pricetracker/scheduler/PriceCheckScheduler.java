package com.seven30games.pricetracker.scheduler;

import com.seven30games.pricetracker.service.PriceCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PriceCheckScheduler {

    private static final Logger log = LoggerFactory.getLogger(PriceCheckScheduler.class);

    private final PriceCheckService priceCheckService;

    public PriceCheckScheduler(PriceCheckService priceCheckService) {
        this.priceCheckService = priceCheckService;
    }

    // Every 30 minutes
    @Scheduled(cron = "0 */30 * * * *")
    public void scheduledPriceCheck() {
        log.info("Scheduler triggered — running price check for all active products");
        priceCheckService.checkAllActiveProducts();
        log.info("Scheduler run complete");
    }
}
