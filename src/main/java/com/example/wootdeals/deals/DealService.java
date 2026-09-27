package com.example.wootdeals.deals;

import com.example.wootdeals.woot.FeedSnapshot;
import com.example.wootdeals.woot.WootClient;
import com.example.wootdeals.woot.WootFeed;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class DealService {

    /** Biggest dollar savings first; ties broken by the higher percentage off. */
    static final Comparator<Deal> BEST_DEAL_FIRST =
            Comparator.comparing(Deal::savings, Comparator.reverseOrder())
                    .thenComparing(Deal::percentOff, Comparator.reverseOrder());

    private final WootClient wootClient;

    public DealService(WootClient wootClient) {
        this.wootClient = wootClient;
    }

    public DealsResponse bestDeals(WootFeed feed, boolean includeSoldOut, int limit) {
        FeedSnapshot snapshot = wootClient.fetchFeed(feed);

        // Streams are Java's .filter/.map/.sort chain. flatMap(Optional::stream) drops the
        // empties, like .map(...).filter(Boolean) would in JS.
        List<Deal> deals = snapshot.items().stream()
                .filter(item -> includeSoldOut || !item.soldOut())
                .map(Deal::from)
                .flatMap(Optional::stream)
                .sorted(BEST_DEAL_FIRST)
                .limit(limit)
                .toList();

        return new DealsResponse(feed.apiName(), snapshot.marketingName(), snapshot.fetchedAt(), deals.size(), deals);
    }
}
