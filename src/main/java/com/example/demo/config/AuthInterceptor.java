package com.example.demo.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        
        // Exclude console endpoints and static resources inside interceptor code for safety
        String uri = request.getRequestURI();
        if (uri.startsWith("/login") || uri.startsWith("/register") || uri.startsWith("/h2-console") || 
            uri.startsWith("/css/") || uri.startsWith("/js/") || uri.startsWith("/favicon.ico")) {
            return true;
        }

        if (session.getAttribute("user") == null) {
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }
}
