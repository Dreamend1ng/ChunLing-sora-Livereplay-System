package com.dreamending.replaylive.config;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 设置跨域响应头
        // 替换为实际的前端域名和端口，例如 http://localhost:8080
        //response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        //response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        //response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        //response.setHeader("Access-Control-Allow-Credentials", "true");
        //response.setHeader("Access-Control-Max-Age", "3600");

        // 放行 OPTIONS 预检请求
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            return true;
        }

        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        if (username == null) {
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("00000");
            return false;
        }
        return true;
    }
}