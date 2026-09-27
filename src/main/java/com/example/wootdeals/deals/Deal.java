package com.example.wootdeals.deals;

import com.example.wootdeals.woot.WootFeedItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * A Woot offer with its savings calculated. This is the shape our API returns.
 *
 * savings    = listPrice - salePrice  (dollars saved; this drives the ranking)
 * percentOff = savings / listPrice * 100
 *
 * When an offer has several variants at different prices, the lowest list price and the
 * lowest sale price are compared, and hasPriceRange is set so the UI can say "from".
 */
public record Deal(
        String offerId,
        String title,
        String subtitle,
        String url,
        String photo,
        String condition,
        BigDecimal salePrice,
        BigDecimal listPrice,
        BigDecimal savings,
        BigDecimal percentOff,
        boolean hasPriceRange,
        boolean soldOut,
        OffsetDateTime endDate) {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    /**
     * Converts a raw feed item into a Deal. Returns Optional.empty() when the item can't be
     * ranked: no list price to compare against, or no actual discount.
     */
    public static Optional<Deal> from(WootFeedItem item) {
        if (item.listPrice() == null || item.salePrice() == null) {
            return Optional.empty();
        }
        BigDecimal list = item.listPrice().low();
        BigDecimal sale = item.salePrice().low();
        if (list == null || sale == null) {
            return Optional.empty();
        }

        BigDecimal savings = list.subtract(sale);
        if (savings.signum() <= 0) {
            return Optional.empty(); // priced at or above list: not a deal
        }
        BigDecimal percentOff = savings.multiply(ONE_HUNDRED).divide(list, 1, RoundingMode.HALF_UP);

        return Optional.of(new Deal(
                item.offerId(),
                item.title(),
                item.subtitle(),
                item.url(),
                item.photo(),
                item.condition(),
                sale,
                list,
                savings,
                percentOff,
                item.listPrice().isRange() || item.salePrice().isRange(),
                item.soldOut(),
                item.endDate()));
    }
}
