package com.dreamending.replaylive.controller;

import com.dreamending.replaylive.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AdminController {
    @Autowired
    private AdminService adminService;

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
}

