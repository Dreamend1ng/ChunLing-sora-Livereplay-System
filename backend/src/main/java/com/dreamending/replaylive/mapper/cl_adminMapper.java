package com.dreamending.replaylive.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dreamending.replaylive.entity.AdminUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface cl_adminMapper extends BaseMapper<AdminUser> {
    // 继承BaseMapper已包含selectOne方法
}

/*
    @com.dreamending.replaylive.mapper
    @Author: Sun Weize - 19393
    @date 2025-04-08  10:37
public interface cl_adminMapper {
    AdminUser AdminLogin(String username, String password);
}*/
