package com.dreamending.replaylive.controller;

import com.dreamending.replaylive.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/getAll")
    public Object getAllUser() {
        return userService.getAllUser();
    }
    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        boolean isValid = userService.validatePassword(username, password);
        return isValid ? "1" : "0";
    }
    @PostMapping("/register")
    public String register(@RequestBody Map<String, String> registerRequest) {
        String username = registerRequest.get("username");
        String password = registerRequest.get("password");
        String mail = registerRequest.get("mail");

        // 基本参数校验
        if (username == null || username.isEmpty() ||
            password == null || password.isEmpty() ||
            mail == null || mail.isEmpty()) {
            return "0"; // 参数不完整
        }
        // 调用注册服务
        boolean isSuccess = userService.registerUser(username, password, mail);
        return isSuccess ? "1" : "0";
    }
    @GetMapping("/roleCheck")
    public String roleCheck(@RequestParam String username) {  // 添加请求参数
        if (username == null || username.isEmpty()) {
            return "0";  // 空值校验
        }
        return userService.roleCheck(username) ? "1" : "0";  // 调用服务层方法
    }
}
