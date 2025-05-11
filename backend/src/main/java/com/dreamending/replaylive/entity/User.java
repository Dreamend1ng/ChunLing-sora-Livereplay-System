package com.dreamending.replaylive.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.UUID;

/*
    @com.dreamending.replaylive.entity
    @Author: Sun Weize - 19393
    @date 2025-04-01  13:32
*/

@TableName("cl_users")
public class User {
    @TableField(value = "id")
    @TableId(type = IdType.ASSIGN_UUID) // 不指定生成策略，由程序或数据库处理
    public String id;
    private String username;
    private String password;
    private String mail;
    private String role;
    //private String testID = String.valueOf(id);
    public User(){
        this.id = UUID.randomUUID().toString();
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", mail='" + mail + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
