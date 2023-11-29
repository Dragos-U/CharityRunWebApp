package com.bearsoft.charityrun.aspects;

import com.bearsoft.charityrun.exceptions.ratelimiter.RateLimiterException;
import com.bearsoft.charityrun.services.security.interfaces.ApiRateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitingAspect {

    private final ApiRateLimiter apiRateLimiter;

    @Pointcut("@annotation(com.bearsoft.charityrun.aspects.RateLimited)")
    public void rateLimit(){}

    @Around("rateLimit()")
    public Object applyRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Intercepted method: " + joinPoint.getSignature().getName());
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String clientIp = request.getRemoteAddr();

        if (apiRateLimiter.isLimitExceeded(clientIp)) {
            throw new RateLimiterException("Rate limit exceeded");
        }

        return joinPoint.proceed();
    }

}
