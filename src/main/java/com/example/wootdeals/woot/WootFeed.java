package com.example.wootdeals.woot;

import java.util.Arrays;

/**
 * The feed names Woot's API supports. Java enums can carry fields and methods,
 * which makes them much more capable than TypeScript enums.
 */
public enum WootFeed {
    ALL("All"),
    CLEARANCE("Clearance"),
    COMPUTERS("Computers"),
    ELECTRONICS("Electronics"),
    FEATURED("Featured"),
    HOME("Home"),
    GOURMET("Gourmet"),
    SHIRTS("Shirts"),
    SPORTS("Sports"),
    TOOLS("Tools"),
    WOOTOFF("Wootoff");

    private final String apiName;

    WootFeed(String apiName) {
        this.apiName = apiName;
    }

    /** The exact name Woot expects in the URL path. */
    public String apiName() {
        return apiName;
    }

    /** Case-insensitive lookup, so "electronics" and "Electronics" both work. */
    public static WootFeed fromApiName(String name) {
        return Arrays.stream(values())
                .filter(feed -> feed.apiName.equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new UnknownFeedException(name));
    }
}
