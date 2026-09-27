package com.example.wootdeals.woot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Verifies our records map Woot's PascalCase JSON (sample taken from Woot's API docs). */
class WootFeedResponseJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void mapsWootFeedJson() throws Exception {
        String json = """
                {
                  "Items": [
                    {
                      "Categories": ["HOME", "TOOLS"],
                      "Condition": null,
                      "EndDate": "2018-10-10T05:00:00+00:00",
                      "IsSoldOut": true,
                      "ListPrice": { "Minimum": 49.99, "Maximum": 49.99 },
                      "OfferId": "2a3deba2-6f5a-43df-875a-ea84709c9a30",
                      "SalePrice": { "Minimum": 39.99, "Maximum": 39.99 },
                      "Title": "TP-Link Smart LED Light Bulb (2-Pack)",
                      "Url": "https://tools.woot.com/offers/tp-link-smart-led-light-bulb-2-pack-3",
                      "SomeFieldWeDoNotModel": "ignored"
                    },
                    {
                      "ListPrice": null,
                      "OfferId": "33fb06a8-6c83-47c6-bcdc-ddf8d6780dea",
                      "SalePrice": { "Minimum": 19.99, "Maximum": 19.99 },
                      "Title": "1000 Lumen Viper Rechargeable Flashlight 2-PK"
                    }
                  ],
                  "MarketingName": "Tools & Garden",
                  "TotalPages": 1
                }
                """;

        WootFeedResponse response = objectMapper.readValue(json, WootFeedResponse.class);

        assertThat(response.marketingName()).isEqualTo("Tools & Garden");
        assertThat(response.items()).hasSize(2);

        WootFeedItem bulb = response.items().getFirst();
        assertThat(bulb.listPrice().low()).isEqualByComparingTo("49.99");
        assertThat(bulb.salePrice().low()).isEqualByComparingTo("39.99");
        assertThat(bulb.soldOut()).isTrue();
        assertThat(bulb.endDate()).isNotNull();

        WootFeedItem flashlight = response.items().get(1);
        assertThat(flashlight.listPrice()).isNull();
        assertThat(flashlight.soldOut()).isFalse(); // missing flag means available
    }
}
