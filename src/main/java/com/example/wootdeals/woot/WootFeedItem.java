package com.example.wootdeals.woot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * One offer from GET /feed/{feedname}. Woot uses PascalCase JSON keys, so each component
 * maps its key explicitly. Wrapper types (Boolean) are used where Woot may omit the field.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record WootFeedItem(
        @JsonProperty("OfferId") String offerId,
        @JsonProperty("Title") String title,
        @JsonProperty("Subtitle") String subtitle,
        @JsonProperty("Url") String url,
        @JsonProperty("Photo") String photo,
        @JsonProperty("Condition") String condition,
        @JsonProperty("Categories") List<String> categories,
        @JsonProperty("ListPrice") PriceRange listPrice,
        @JsonProperty("SalePrice") PriceRange salePrice,
        @JsonProperty("IsSoldOut") Boolean isSoldOut,
        @JsonProperty("IsFeatured") Boolean isFeatured,
        @JsonProperty("IsWootOff") Boolean isWootOff,
        @JsonProperty("StartDate") OffsetDateTime startDate,
        @JsonProperty("EndDate") OffsetDateTime endDate) {

    /** Null-safe: a missing IsSoldOut flag is treated as "still available". */
    public boolean soldOut() {
        return Boolean.TRUE.equals(isSoldOut);
    }
}
