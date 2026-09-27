package com.example.wootdeals.woot;

import com.example.wootdeals.config.WootProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.List;

/**
 * Thin wrapper around Woot's REST API. RestClient is Spring's modern, fluent HTTP client;
 * it plays the role fetch/axios plays in your JS apps.
 */
@Component
public class WootClient {

    public static final String FEED_CACHE = "wootFeeds";
    private static final Logger log = LoggerFactory.getLogger(WootClient.class);

    private final RestClient restClient;

    // Constructor injection: Spring supplies the builder and the bound properties
    public WootClient(RestClient.Builder builder, WootProperties properties) {
        this.restClient = builder
                .baseUrl(properties.baseUrl())
                .defaultHeader("x-api-key", properties.apiKey())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * Fetches every offer in a feed. Omitting the "page" query parameter makes Woot return
     * all offers at once. Results are cached per feed (see FeedCacheRefresher for expiry),
     * which keeps us well under Woot's limit of 1,000 requests per day.
     *
     * Note: @Cacheable works through a Spring proxy, so it only applies when this method is
     * called from another bean, not from inside this class.
     */
    @Cacheable(FEED_CACHE)
    public FeedSnapshot fetchFeed(WootFeed feed) {
        log.info("Fetching Woot feed '{}'", feed.apiName());

        WootFeedResponse response = restClient.get()
                .uri("/feed/{feedName}", feed.apiName())
                .retrieve() // throws RestClientResponseException on 4xx/5xx
                .body(WootFeedResponse.class);

        if (response == null) {
            return new FeedSnapshot(feed.apiName(), List.of(), Instant.now());
        }
        log.info("Received {} offers from feed '{}'", response.items().size(), feed.apiName());
        return new FeedSnapshot(response.marketingName(), response.items(), Instant.now());
    }
}
