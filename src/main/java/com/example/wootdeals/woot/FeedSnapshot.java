package com.example.wootdeals.woot;

import java.time.Instant;
import java.util.List;

/** A feed as fetched at a point in time. This is what gets cached. */
public record FeedSnapshot(String marketingName, List<WootFeedItem> items, Instant fetchedAt) {
}
