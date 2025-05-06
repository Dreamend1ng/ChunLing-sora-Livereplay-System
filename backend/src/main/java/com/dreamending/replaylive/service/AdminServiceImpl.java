package com.dreamending.replaylive.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dreamending.replaylive.entity.AdminUser;
import com.dreamending.replaylive.mapper.cl_adminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private cl_adminMapper adminMapper;

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
}
