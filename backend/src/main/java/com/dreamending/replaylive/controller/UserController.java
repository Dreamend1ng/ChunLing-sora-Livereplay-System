package com.dreamending.replaylive.controller;

import com.dreamending.replaylive.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import jakarta.servlet.http.HttpSession;

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
    public String login(@RequestBody Map<String, String> loginRequest, HttpSession session) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        boolean isValid = userService.validatePassword(username, password);
        if (isValid) {
            session.setAttribute("username", username);
            return "1";
        }
        return "0";
    }

    @GetMapping("/currentUser")
    public String getCurrentUser(HttpSession session) {
        String username = (String) session.getAttribute("username");
        System.out.println("Login in:" + username);
        return username != null ? username : "0";
    }
}
