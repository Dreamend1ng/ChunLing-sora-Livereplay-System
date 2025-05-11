package com.dreamending.replaylive.service;

import com.dreamending.replaylive.entity.Comment;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;

/*
    @com.dreamending.replaylive.service
    @Author: Sun Weize - 19393
    @date 2025-04-22  08:13
*/public interface CommentService {
    List<String> getComments(String video_id);
    List<String> getAllComments();
    List<String> sendComment(String video_id, String username, String comment);
    void delComment(String comment_id);
}