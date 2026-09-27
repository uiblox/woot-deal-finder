package com.example.wootdeals.web;

import com.example.wootdeals.woot.UnknownFeedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

/**
 * Global error handling, similar to an Express error middleware. Returns RFC 9457
 * "problem detail" JSON so clients get a consistent error shape.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(UnknownFeedException.class)
    public ProblemDetail handleUnknownFeed(UnknownFeedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Unknown feed");
        return problem;
    }

    /** Woot answered, but with an error status. */
    @ExceptionHandler(RestClientResponseException.class)
    public ProblemDetail handleWootError(RestClientResponseException ex) {
        int status = ex.getStatusCode().value();
        log.warn("Woot API returned HTTP {}: {}", status, ex.getResponseBodyAsString());

        String detail = switch (status) {
            case 401, 403 -> "Woot rejected the request. Check that WOOT_API_KEY holds a valid key.";
            case 429 -> "Woot's rate limit was reached (1 request/second, 1,000/day). Try again later.";
            default -> "Woot returned HTTP " + status + ".";
        };
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, detail);
        problem.setTitle("Woot API error");
        return problem;
    }

    /** Woot couldn't be reached at all (DNS, timeout, connection refused). */
    @ExceptionHandler(ResourceAccessException.class)
    public ProblemDetail handleWootUnreachable(ResourceAccessException ex) {
        log.warn("Could not reach Woot API", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_GATEWAY, "Could not reach the Woot API. Check your network connection.");
        problem.setTitle("Woot API unreachable");
        return problem;
    }
}
