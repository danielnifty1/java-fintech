package com.example.demo.shared.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    // runs before the controller method
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        request.setAttribute("startTime", System.currentTimeMillis());
        logger.info("→ {} {}", request.getMethod(), request.getRequestURI());
        return true; // return false to block the request
    }

    // runs after the controller method
    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) {
        logger.info("← {} {} | status: {}",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus()
        );
    }

    // runs after the response is sent — good for cleanup and timing
    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        long startTime = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;

        logger.info("✓ {} {} completed in {}ms",
                request.getMethod(),
                request.getRequestURI(),
                duration
        );

        if (ex != null) {
            logger.error("✗ Exception during request: {}", ex.getMessage());
        }
    }
}