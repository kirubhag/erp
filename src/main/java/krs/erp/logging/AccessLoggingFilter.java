package krs.erp.logging;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Logs HTTP access information in JSON lines to the `ACCESS` logger.
 * Be careful to avoid sensitive data. Authorization and cookies are masked.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class AccessLoggingFilter extends OncePerRequestFilter {

    private static final Logger ACCESS_LOGGER = LogManager.getLogger("ACCESS");
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final DateTimeFormatter TS_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    @PostConstruct
    public void init() {
        ACCESS_LOGGER.info("AccessLoggingFilter initialized");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        long start = System.nanoTime();
        String requestId = UUID.randomUUID().toString();
        try {
            // proceed
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long durationMs = (System.nanoTime() - start) / 1_000_000;
            Map<String, Object> log = new LinkedHashMap<>();

            // Timestamp
            log.put("timestamp", TS_FORMATTER.format(Instant.now().atOffset(ZoneOffset.UTC)));
            log.put("requestId", requestId);

            // Trace id if present (instrumentation or gateway may set this)
            String traceId = request.getHeader("X-Request-Id");
            if (traceId == null || traceId.isBlank()) traceId = request.getHeader("X-B3-TraceId");
            if (traceId != null && !traceId.isBlank()) log.put("traceId", traceId);

            // Request metadata
            log.put("method", request.getMethod());
            log.put("path", request.getRequestURI());
            log.put("protocol", request.getProtocol());
            log.put("query", request.getQueryString());

            // Client info
            log.put("clientIp", extractClientIp(request));
            log.put("userAgent", request.getHeader("User-Agent"));
            log.put("referer", request.getHeader("Referer"));

            // Small, non-sensitive subset of request headers
            Map<String, String> requestHeaders = new LinkedHashMap<>();
            if (request.getHeader("User-Agent") != null) requestHeaders.put("userAgent", request.getHeader("User-Agent"));
            if (request.getHeader("Referer") != null) requestHeaders.put("referer", request.getHeader("Referer"));
            if (request.getHeader("Accept") != null) requestHeaders.put("accept", request.getHeader("Accept"));
            if (request.getContentType() != null) requestHeaders.put("contentType", request.getContentType());
            if (request.getHeader("Content-Length") != null) requestHeaders.put("contentLength", request.getHeader("Content-Length"));
            if (!requestHeaders.isEmpty()) log.put("requestHeaders", requestHeaders);

            // Auth / session metadata (masked)
            String auth = maskHeader(request.getHeader("Authorization"));
            if (auth != null) log.put("auth", auth);
            String cookie = maskHeader(request.getHeader("Cookie"));
            if (cookie != null) log.put("cookie", cookie);

            // Request body summary (don't log raw sensitive body)
            int reqSize = wrappedRequest.getContentAsByteArray() == null ? 0 : wrappedRequest.getContentAsByteArray().length;
            Map<String, Object> reqBody = new LinkedHashMap<>();
            reqBody.put("contentType", request.getContentType());
            reqBody.put("size", reqSize);
            if (reqSize > 0) {
                String snippet = safeSnippet(wrappedRequest.getContentAsByteArray());
                if (snippet != null) reqBody.put("snippet", snippet);
            }
            log.put("requestBody", reqBody);

            // Server-side processing info
            Object handler = request.getAttribute("org.springframework.web.servlet.HandlerMapping.bestMatchingHandler");
            if (handler != null) log.put("handler", handler.toString());
            log.put("durationMs", durationMs);

            // Response info
            log.put("status", wrappedResponse.getStatus());
            int respSize = wrappedResponse.getContentAsByteArray() == null ? 0 : wrappedResponse.getContentAsByteArray().length;
            log.put("responseSize", respSize);
            if (wrappedResponse.getContentType() != null) log.put("responseContentType", wrappedResponse.getContentType());

            // Infrastructure context
            log.put("instance", request.getLocalName());
            log.put("env", System.getenv("SPRING_PROFILES_ACTIVE"));

            try {
                String json = MAPPER.writeValueAsString(log);
                ACCESS_LOGGER.info(json);
            } catch (Exception e) {
                ACCESS_LOGGER.error("Failed to serialize access log", e);
            }

            // important: copy response body back to original response
            wrappedResponse.copyBodyToResponse();
        }
    }

    private static String safeSnippet(byte[] content) {
        if (content == null || content.length == 0) return null;
        int len = Math.min(content.length, 512);
        String s = new String(content, 0, len, StandardCharsets.UTF_8).replaceAll("\n", "\\n").replaceAll("\r", "");
        // Avoid logging common sensitive fields
        if (s.toLowerCase().contains("password") || s.toLowerCase().contains("token") || s.toLowerCase().contains("authorization")) {
            return "[redacted]";
        }
        return s;
    }

    private static String extractClientIp(HttpServletRequest request) {
        String header = request.getHeader("X-Forwarded-For");
        if (header != null && !header.isBlank()) {
            return header.split(",")[0].trim();
        }
        header = request.getHeader("X-Real-IP");
        if (header != null && !header.isBlank()) return header.trim();
        return request.getRemoteAddr();
    }

    private static String maskHeader(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        if (trimmed.length() == 0) return null;
        // Hash long tokens to avoid exposing them
        if (trimmed.length() > 20) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] digest = md.digest(trimmed.getBytes(StandardCharsets.UTF_8));
                return "sha256:" + Base64.getUrlEncoder().withoutPadding().encodeToString(digest).substring(0, 16);
            } catch (Exception e) {
                return "[masked]";
            }
        }
        return "[masked]";
    }
}
