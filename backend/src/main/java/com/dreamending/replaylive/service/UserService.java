package com.dreamending.replaylive.service;

import com.dreamending.replaylive.entity.User;

import java.util.List;
import java.util.UUID;
/*
    @com.dreamending.replaylive.service
    @Author: Sun Weize - 19393
    @date 2025-04-01  14:37
*/public interface UserService {
    List<String> getAllUser();
    boolean validatePassword(String username, String encryptedPassword);
    boolean registerUser(String username, String password, String mail);
    boolean roleCheck(String username);
    String changePassword(String username, String newPassword);
    boolean roleUpdate(String username, String role);
    boolean copyUserToAdmin(String username);
}
