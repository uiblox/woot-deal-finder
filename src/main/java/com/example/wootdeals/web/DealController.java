package com.example.wootdeals.web;

import com.example.wootdeals.deals.DealService;
import com.example.wootdeals.deals.DealsResponse;
import com.example.wootdeals.woot.WootFeed;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DealController {

    private static final int MAX_LIMIT = 500;

    private final DealService dealService;

    public DealController(DealService dealService) {
        this.dealService = dealService;
    }

    /**
     * GET /api/deals?feed=Electronics&includeSoldOut=false&limit=50
     * Returns current offers ranked by dollar savings, best deal first.
     */
    @GetMapping("/deals")
    public DealsResponse deals(
            @RequestParam(defaultValue = "All") String feed,
            @RequestParam(defaultValue = "false") boolean includeSoldOut,
            @RequestParam(defaultValue = "50") int limit) {
        return dealService.bestDeals(WootFeed.fromApiName(feed), includeSoldOut, Math.clamp(limit, 1, MAX_LIMIT));
    }

    /** GET /api/feeds lists the feed names the UI can offer. */
    @GetMapping("/feeds")
    public List<String> feeds() {
        return Arrays.stream(WootFeed.values()).map(WootFeed::apiName).toList();
    }
}
