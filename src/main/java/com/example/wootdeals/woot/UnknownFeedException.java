package com.example.wootdeals.woot;

import java.util.Arrays;
import java.util.stream.Collectors;

public class UnknownFeedException extends RuntimeException {

    public UnknownFeedException(String requested) {
        super("Unknown feed '" + requested + "'. Valid feeds: "
                + Arrays.stream(WootFeed.values()).map(WootFeed::apiName).collect(Collectors.joining(", ")));
    }
}
