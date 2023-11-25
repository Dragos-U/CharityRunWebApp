package com.bearsoft.charityrun.exceptions.ratelimiter;

public class RateLimiterException extends RuntimeException{

    public RateLimiterException(String message) {
        super(message);
    }
}
