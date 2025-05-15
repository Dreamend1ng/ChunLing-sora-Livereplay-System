package com.dreamending.replaylive.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dreamending.replaylive.entity.AdminUser;
import com.dreamending.replaylive.mapper.cl_adminMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.PrintStream;
import java.nio.file.AccessDeniedException;
import java.util.Base64;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private cl_adminMapper adminMapper;
    private static final StringBuilder runtimeLogs = new StringBuilder();
    @Override
    public boolean validatePassword(String username, String password) {
        String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes());

        QueryWrapper<AdminUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("username", "password") // 明确指定查询字段
                   .eq("username", username)
                   .eq("password", encryptedPassword);

        return adminMapper.selectOne(queryWrapper) != null;
    }
    @Override
    public boolean adminCheck(String username) {
        QueryWrapper<AdminUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("whoUse", username);
        return adminMapper.selectCount(queryWrapper) > 0;
    }
    @Override
    public boolean loginVerify(String encryptedUsername) {
        System.out.println("Get the request of admin login:" + encryptedUsername);
        // 双重Base64解码
        String decodedUsername = new String(Base64.getDecoder().decode(encryptedUsername));
        String finalUsername = new String(Base64.getDecoder().decode(decodedUsername));
        System.out.println(finalUsername + " wants to login, querying...");
        // 构建查询条件
        QueryWrapper<AdminUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", finalUsername);

        return adminMapper.selectCount(queryWrapper) > 0;
    }
    @Override
    public String serviceStatus() {
        return runtimeLogs.toString();
    }

    // 添加日志收集方法（需要初始化日志重定向）
    @PostConstruct
    private void initLogCapture() {
        System.setOut(new PrintStream(System.out) {
            public void println(String x) {
                runtimeLogs.append(x).append("\n");
                super.println(x);
            }
        });
    }
}
