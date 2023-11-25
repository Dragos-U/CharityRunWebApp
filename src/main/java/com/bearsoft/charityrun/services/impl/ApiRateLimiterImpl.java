package com.bearsoft.charityrun.services.impl;

import com.bearsoft.charityrun.services.ApiRateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class ApiRateLimiterImpl implements ApiRateLimiter {

    private final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS_PER_MINUTE = 3;

    @Override
    public boolean isLimitExceeded(String clientIp) {
        AtomicInteger requests = requestCounts.computeIfAbsent(clientIp, k-> new AtomicInteger(0));
        if(requests.incrementAndGet() > MAX_REQUESTS_PER_MINUTE){
            return true;
        }
        requestCounts.entrySet().removeIf(entry -> entry.getValue().incrementAndGet() > MAX_REQUESTS_PER_MINUTE);
        return false;
    }
}
