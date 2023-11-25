package com.bearsoft.charityrun.security.config;


import com.bearsoft.charityrun.exceptions.ratelimiter.RateLimiterException;
import com.bearsoft.charityrun.services.ApiRateLimiter;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
@Order(1)
public class RateLimitingFilter implements Filter {

    private final ApiRateLimiter rateLimiter;

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String clientIp = servletRequest.getRemoteAddr();
        if (rateLimiter.isLimitExceeded(clientIp)) {
            response.setStatus(429);
            response.getWriter().write("Rate limit exceeded");
            throw new RateLimiterException("Rate limit exceeded");
        }

        filterChain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
