package com.bearsoft.charityrun.services.security.interfaces;

public interface ApiRateLimiter {

    boolean isLimitExceeded(String clientIp);
}
