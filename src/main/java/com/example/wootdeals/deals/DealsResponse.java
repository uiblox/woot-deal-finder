package com.example.wootdeals.deals;

import java.time.Instant;
import java.util.List;

/** Response body for GET /api/deals. */
public record DealsResponse(
        String feed,
        String marketingName,
        Instant fetchedAt,
        int count,
        List<Deal> deals) {
}
