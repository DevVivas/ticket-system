package com.ticketsystem.eventos.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    private static final String START_TIME = "requestStartTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String clientIp = request.getRemoteAddr();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        request.setAttribute(START_TIME, System.currentTimeMillis());
        logger.info("Request started: {} {} from IP {}", method, uri, clientIp);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long startTime = (long) request.getAttribute(START_TIME);
        long duration = System.currentTimeMillis() - startTime;
        int statusCode = response.getStatus();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        logger.info("Request finished: {} {} - Status: {} - Duration: {}ms", method, uri, statusCode, duration);
    }
}
