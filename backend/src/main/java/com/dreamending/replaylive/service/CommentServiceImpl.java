package com.dreamending.replaylive.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dreamending.replaylive.mapper.cl_commentMapper;
import com.dreamending.replaylive.entity.Comment;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private cl_commentMapper commentMapper;

    @Override
    public List<String> getComments(String video_id) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("video_id", video_id)
                   .select("comment_id", "username", "video_id", "comment", "comment_time");

        return commentMapper.selectList(queryWrapper).stream()
                .map(comment -> String.format(
                    "commentID: %s, username: %s, video_id: %s, comment: %s, comment_time: %s",
                    comment.getCommentId(),
                    comment.getUsername(),
                    comment.getVideoId(),
                    comment.getComment(),
                    comment.getCommentTime()))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllComments() {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("comment_id", "username", "video_id", "comment", "comment_time");

        return commentMapper.selectList(queryWrapper).stream()
                .map(comment -> String.format(
                    "commentID: %s, username: %s, video_id: %s, comment: %s, comment_time: %s",
                    comment.getCommentId(),
                    comment.getUsername(),
                    comment.getVideoId(),
                    comment.getComment(),
                    comment.getCommentTime()))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> sendComment(String video_id, String user_id, String content) {
        Comment comment = new Comment();
        comment.setVideoId(video_id);
        comment.setUsername(user_id);
        comment.setComment(content);
        comment.setCommentTime(new Date());
        commentMapper.insert(comment);
        return getComments(video_id);
    }

    @Override
    public void delComment(String comment_id) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        System.out.println("Get Comment_id:\n" + comment_id);
        queryWrapper.eq("comment_id", comment_id);
        System.out.println("Comment will delete:\n" + comment_id);
        commentMapper.delete(queryWrapper);
    }
}

