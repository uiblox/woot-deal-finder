package com.example.wootdeals.deals;

import com.example.wootdeals.woot.FeedSnapshot;
import com.example.wootdeals.woot.PriceRange;
import com.example.wootdeals.woot.WootClient;
import com.example.wootdeals.woot.WootFeed;
import com.example.wootdeals.woot.WootFeedItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Plain unit test: no Spring context, just the class under test and a Mockito mock. */
class DealServiceTest {

    private final WootClient wootClient = mock(WootClient.class);
    private final DealService dealService = new DealService(wootClient);

    @BeforeEach
    void setUp() {
        List<WootFeedItem> items = List.of(
                item("Headphones", "100.00", "60.00", false),   // saves 40.00 (40%)
                item("Television", "500.00", "450.00", false),  // saves 50.00 (10%)
                item("Blender", "90.00", "40.00", false),       // saves 50.00 (55.6%), wins the tie
                item("No list price", null, "19.99", false),    // can't rank
                item("Marked up", "20.00", "25.00", false),     // not a deal
                item("Laptop", "1200.00", "300.00", true));     // saves 900.00 but sold out
        when(wootClient.fetchFeed(WootFeed.ALL))
                .thenReturn(new FeedSnapshot("Everything", items, Instant.parse("2026-09-27T12:00:00Z")));
    }

    @Test
    void ranksByDollarSavingsWithPercentageAsTieBreaker() {
        DealsResponse response = dealService.bestDeals(WootFeed.ALL, false, 50);

        assertThat(response.deals())
                .extracting(Deal::title)
                .containsExactly("Blender", "Television", "Headphones");
        assertThat(response.deals().getFirst().savings()).isEqualByComparingTo("50.00");
        assertThat(response.deals().getFirst().percentOff()).isEqualByComparingTo("55.6");
    }

    @Test
    void includesSoldOutOffersOnlyWhenAsked() {
        DealsResponse response = dealService.bestDeals(WootFeed.ALL, true, 50);

        assertThat(response.deals().getFirst().title()).isEqualTo("Laptop");
        assertThat(response.deals().getFirst().soldOut()).isTrue();
    }

    @Test
    void respectsTheLimit() {
        DealsResponse response = dealService.bestDeals(WootFeed.ALL, false, 2);

        assertThat(response.count()).isEqualTo(2);
        assertThat(response.deals()).extracting(Deal::title).containsExactly("Blender", "Television");
    }

    private static WootFeedItem item(String title, String list, String sale, boolean soldOut) {
        return new WootFeedItem(
                title + "-id", title, null, "https://www.woot.com/offers/" + title, null, "New", List.of(),
                price(list), price(sale), soldOut, false, false, null, null);
    }

    private static PriceRange price(String amount) {
        return amount == null ? null : new PriceRange(new BigDecimal(amount), new BigDecimal(amount));
    }
}
