# Woot Deals

A Spring Boot app that pulls current offers from the [Woot Developer API](https://developer.woot.com)
and ranks them by dollar savings (list price minus sale price), best deal first.

## Run it

Requires JDK 21+ and Maven.

```bash
export WOOT_API_KEY=your-key-here     # PowerShell: $env:WOOT_API_KEY="your-key-here"
mvn spring-boot:run
```

Then open http://localhost:8080 for the UI, or call the API directly:

```bash
curl "http://localhost:8080/api/deals"
curl "http://localhost:8080/api/deals?feed=Electronics&limit=10"
curl "http://localhost:8080/api/deals?feed=Home&includeSoldOut=true"
curl "http://localhost:8080/api/feeds"
```

In IntelliJ, add `WOOT_API_KEY` under Run > Edit Configurations > Environment variables.
The key is never stored in the code or in application.yml, so it's safe to commit this project.

Run the tests with `mvn test` (they don't call Woot, so no key is needed).

## API

`GET /api/deals`

| Param | Default | Notes |
|---|---|---|
| `feed` | `All` | All, Clearance, Computers, Electronics, Featured, Home, Gourmet, Shirts, Sports, Tools, Wootoff (case-insensitive) |
| `includeSoldOut` | `false` | Sold-out offers are hidden unless this is true |
| `limit` | `50` | 1 to 500 |

Each deal includes `salePrice`, `listPrice`, `savings`, `percentOff`, `hasPriceRange`, `soldOut`, `url`, and `photo`.
Offers with no list price or no discount are left out because they can't be ranked.

## How ranking works

`savings = listPrice - salePrice`, sorted largest first, with percentage off as the tie-breaker.
When an offer has variants at different prices, the lowest list and lowest sale price are compared.
To rank by percentage instead, change `BEST_DEAL_FIRST` in `DealService`.

## Rate limits

Woot allows 1 request/second and 1,000 requests/day. Each feed is cached in memory and refreshed
every 10 minutes (`woot.cache-refresh-minutes` in application.yml), so browsing costs at most
one Woot call per feed per interval.

## Project layout

```
config/WootProperties        typed woot.* settings, fails fast if the key is missing
woot/WootClient              RestClient call to /feed/{name}, cached with @Cacheable
woot/FeedCacheRefresher      @Scheduled job that clears the cache
woot/*                       records mirroring Woot's JSON, feed enum
deals/Deal                   savings calculation
deals/DealService            filtering and ranking with streams
web/DealController           REST endpoints
web/ApiExceptionHandler      maps errors to ProblemDetail JSON
resources/static/index.html  small UI served by Spring Boot
```
