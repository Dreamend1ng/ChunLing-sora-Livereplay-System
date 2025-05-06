package com.dreamending.replaylive.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dreamending.replaylive.entity.ReplayVideo;
import org.apache.ibatis.annotations.Mapper;

@Mapper


/*
    @com.dreamending.replaylive.mapper
    @Author: Sun Weize - 19393
    @date 2025-04-03  13:22
*/

public interface cl_replayMapper extends BaseMapper<ReplayVideo> {
    // 继承BaseMapper已包含selectOne方法
}
