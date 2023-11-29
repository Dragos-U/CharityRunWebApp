package com.bearsoft.charityrun.services.security;

import com.bearsoft.charityrun.services.security.interfaces.ApiRateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class ApiRateLimiterImpl implements ApiRateLimiter {

    private static class ClientRequestInfo {
        AtomicInteger requestCount = new AtomicInteger(0);
        LocalDateTime windowStartTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    }

    private final ConcurrentHashMap<String, ClientRequestInfo> requestCounts = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS_PER_MINUTE = 10;

    @Override
    public synchronized boolean isLimitExceeded(String clientIp) {
        ClientRequestInfo info = requestCounts.computeIfAbsent(clientIp, k -> new ClientRequestInfo());

        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        if (!currentTime.equals(info.windowStartTime)) {
            info.requestCount.set(0);
            info.windowStartTime = currentTime;
        }

        if (info.requestCount.incrementAndGet() > MAX_REQUESTS_PER_MINUTE) {
            return true;
        }

        return false;
    }
}
