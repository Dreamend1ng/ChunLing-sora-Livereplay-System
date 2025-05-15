package com.dreamending.replaylive.controller;

import com.dreamending.replaylive.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

@RestController
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private HttpServletRequest request;

    @PostMapping("/api/admin/login")
    public String login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        boolean isValid = adminService.validatePassword(username, password);
        return isValid ? "1" : "0";
    }
    @GetMapping("/api/admin/adminCheck")
    public String checkAdmin(@RequestParam String username) {
        return adminService.adminCheck(username) ? "1" : "0";
    }
    @PostMapping("/api/admin/loginVerify")
    public ResponseEntity<String> loginVerify(@RequestParam String encryptedUsername) {
        // 修复1：将clientIP声明移到方法顶部
        String clientIP = request.getRemoteAddr();

        // 修复2：将isLocalRequest改为私有方法（移到类作用域）
        if (!isLocalRequest(clientIP)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("0");
        }

        boolean isValid = adminService.loginVerify(encryptedUsername);
        // 修复3：补全三元表达式
        return ResponseEntity.ok(isValid ? "1" : "0");
    }
    // 将方法移到类作用域中
    private boolean isLocalRequest(String ip) {
        return ip.equals("127.0.0.1") ||
               ip.equals(request.getLocalAddr());
    }
    @GetMapping("/api/admin/serviceStatus")
    public ResponseEntity<String> getServiceStatus() {
        return ResponseEntity.ok(adminService.serviceStatus());
    }

}

