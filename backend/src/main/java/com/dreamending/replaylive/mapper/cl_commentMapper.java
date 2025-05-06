package com.dreamending.replaylive.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dreamending.replaylive.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

/*
    @com.dreamending.replaylive.mapper
    @Author: Sun Weize - 19393
    @date 2025-04-22  08:08
*/
@Mapper
public interface cl_commentMapper extends BaseMapper<Comment> {
    // 继承BaseMapper已包含selectOne方法
}