package com.example.wootdeals.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Typed binding for the {@code woot.*} section of application.yml.
 * Think of it as a validated, typed version of reading process.env in Node.
 */
@ConfigurationProperties(prefix = "woot")
public record WootProperties(
        @DefaultValue("https://developer.woot.com") String baseUrl,
        String apiKey,
        @DefaultValue("10") int cacheRefreshMinutes) {

    // Compact constructor: runs on startup, so a missing key fails fast with a clear message
    public WootProperties {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Missing Woot API key. Set the WOOT_API_KEY environment variable before starting the app.");
        }
        if (cacheRefreshMinutes < 1) {
            throw new IllegalStateException("woot.cache-refresh-minutes must be at least 1");
        }
    }
}
