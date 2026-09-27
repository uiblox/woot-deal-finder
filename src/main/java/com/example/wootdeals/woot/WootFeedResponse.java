package com.example.wootdeals.woot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** The top-level body returned by GET /feed/{feedname}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record WootFeedResponse(
        @JsonProperty("Items") List<WootFeedItem> items,
        @JsonProperty("MarketingName") String marketingName,
        @JsonProperty("TotalPages") Integer totalPages) {

    public WootFeedResponse {
        items = items == null ? List.of() : List.copyOf(items); // never hand out a null list
    }
}
