package com.example.wootdeals.woot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Woot reports prices as a range because one offer can contain several variants
 * (sizes, colors, etc.) at different prices. Single-price offers have minimum == maximum.
 *
 * BigDecimal is used instead of double because floating point can't represent money exactly
 * (the same 0.1 + 0.2 problem you know from JavaScript).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PriceRange(
        @JsonProperty("Minimum") BigDecimal minimum,
        @JsonProperty("Maximum") BigDecimal maximum) {

    /** The lowest price in the range, or null if Woot sent no values. */
    public BigDecimal low() {
        return minimum != null ? minimum : maximum;
    }

    /** True when the offer's variants have different prices. */
    public boolean isRange() {
        return minimum != null && maximum != null && minimum.compareTo(maximum) != 0;
    }
}
