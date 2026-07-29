package com.ayaan.UrlShortner.Config;

import com.ayaan.UrlShortner.Entity.CustomUserDetails;
import com.ayaan.UrlShortner.Entity.Enums.PlanType;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingConfig extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Only rate-limit URL creation — not every endpoint
        if (!request.getRequestURI().equals("/api/urls") || !"POST".equals(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails principal)) {
            filterChain.doFilter(request, response); // unauthenticated — JwtFilter/security will reject separately
            return;
        }

        String key = principal.getUsername();
        PlanType plan = principal.getUser().getPlanType();

        Bucket bucket = buckets.computeIfAbsent(key, k -> newBucket(plan));

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429); // Too Many Requests
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"error\":\"Too Many Requests\",\"message\":\"Rate limit exceeded. Try again later.\"}");
        }
    }

    private Bucket newBucket(PlanType plan) {
        // FREE: 5 link creations / minute. PREMIUM: 30 / minute.
        int capacity = plan == PlanType.PREMIUM ? 30 : 5;
        Bandwidth limit = Bandwidth.classic(capacity,
                io.github.bucket4j.Refill.greedy(capacity, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }
}