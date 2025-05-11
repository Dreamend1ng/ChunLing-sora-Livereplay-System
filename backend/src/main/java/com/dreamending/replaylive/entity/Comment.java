package com.dreamending.replaylive.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import java.util.Date;
import java.util.Random;

/*
    @com.dreamending.replaylive.entity
    @Author: Sun Weize - 19393
    @date 2025-04-01  13:32
*/
@TableName("cl_comment")
public class Comment {
    @TableId(type = IdType.INPUT)
    public String comment_id;
    public String username;
    public String video_id;
    public String comment;
    public Date comment_time;

    @Override
    public String toString() {
        return "Comment{" +
                "comment_id='" + comment_id + '\'' +
                ", username='" + username + '\'' +
                ", video_id='" + video_id + '\'' +
                ", comment='" + comment + '\'' +
                ", comment_time=" + comment_time +
                '}';
    }
    public Comment() {
        // 生成时间戳基础ID（格式：时间戳_随机数）
        long timestamp = System.currentTimeMillis();
        int random = new Random().nextInt(9999);
        this.comment_id = String.format("%d_%04d", timestamp, random);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getComment_time() {
        return comment_time;
    }

    public void setComment_time(Date comment_time) {
        this.comment_time = comment_time;
    }


}
