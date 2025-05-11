package com.dreamending.replaylive.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;


/*
    @com.dreamending.replaylive.entity
    @Author: Sun Weize - 19393
    @date 2025-04-01  13:32
*/

@Data
@TableName("cl_admin")
public class AdminUser {
    @TableField(value = "id")
    @TableId(value = "id",type = IdType.AUTO) //主键自增id
    private Integer id;
    private String username;
    private String password;
    private String whoUse;

    // toString()可以保留，但@Data默认也会生成toString()
    // 如果你需要自定义格式，可以保留这个实现

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWhoUse() {
        return whoUse;
    }

    public void setWhoUse(String whoUse) {
        this.whoUse = whoUse;
    }

    @Override
    public String toString() {
        return "AdminUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='[PROTECTED]'" + // 建议不直接显示密码
                ", whoUse='" + whoUse + '\'' +
                '}';
    }
}

