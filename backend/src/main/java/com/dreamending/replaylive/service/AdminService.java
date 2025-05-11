package com.dreamending.replaylive.service;

import com.dreamending.replaylive.entity.AdminUser;
import com.dreamending.replaylive.controller.AdminController;
/*
    @com.dreamending.replaylive.service
    @Author: Sun Weize - 19393
    @date 2025-04-01  14:41
*/public interface AdminService {
    boolean validatePassword(String username, String encryptedPassword);
    boolean adminCheck(String username);
}
