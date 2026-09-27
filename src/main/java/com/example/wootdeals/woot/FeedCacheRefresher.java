package com.example.wootdeals.woot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Spring's default in-memory cache never expires entries, so this job clears the feed cache
 * on a fixed interval. The next request after a clear fetches fresh offers from Woot.
 */
@Component
public class FeedCacheRefresher {

    private static final Logger log = LoggerFactory.getLogger(FeedCacheRefresher.class);

    private final CacheManager cacheManager;

    public FeedCacheRefresher(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Scheduled(
            initialDelayString = "${woot.cache-refresh-minutes}",
            fixedRateString = "${woot.cache-refresh-minutes}",
            timeUnit = TimeUnit.MINUTES)
    public void clearFeedCache() {
        Cache cache = cacheManager.getCache(WootClient.FEED_CACHE);
        if (cache != null) {
            cache.clear();
            log.debug("Cleared Woot feed cache");
        }
    }
}
