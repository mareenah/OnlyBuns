package com.example.onlybuns.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class RateLimitingFilter implements Filter {

    private static final int MAX_REQUESTS_PER_MINUTE = 5;
    private static final long TIME_WINDOW_MS = 60_000; // 1 minute
    private static final long STALE_THRESHOLD_MS = 5 * 60_000;

    // Stores per-IP request info (count + timestamp)
    private final Map<String, RequestInfo> requestCountsPerIpAddress = new ConcurrentHashMap<>();

    private static class RequestInfo {
        AtomicInteger count;
        long timestamp;

        RequestInfo() {
            this.count = new AtomicInteger(1); // First request
            this.timestamp = System.currentTimeMillis();
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String clientIpAddress = httpServletRequest.getRemoteAddr();
        long currentTime = System.currentTimeMillis();

        // Update request info atomically
        requestCountsPerIpAddress.compute(clientIpAddress, (ip, info) -> {
            if (info == null || (currentTime - info.timestamp) > TIME_WINDOW_MS) {
                // New window starts
                return new RequestInfo();
            } else {
                // Within current window, increment request count
                info.count.incrementAndGet();
                return info;
            }
        });

        // Check if the request limit has been exceeded
        RequestInfo requestInfo = requestCountsPerIpAddress.get(clientIpAddress);
        if (requestInfo.count.get() > MAX_REQUESTS_PER_MINUTE) {
            System.out.println("Blocked IP: " + clientIpAddress);
            httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpServletResponse.getWriter().write("Too many requests. Please try again later.");
            return;
        }

        chain.doFilter(request, response);
    }

    @Scheduled(fixedRate = 7 * 60 * 1000) // Every 7 minutes clean old IPs
    public void cleanupOldEntries() {
        long now = System.currentTimeMillis();
        requestCountsPerIpAddress.entrySet().removeIf(entry -> (now - entry.getValue().timestamp) > STALE_THRESHOLD_MS);
    }
}
