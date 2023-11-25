package com.bearsoft.charityrun.services;

public interface ApiRateLimiter {

    boolean isLimitExceeded(String clientIp);
}
