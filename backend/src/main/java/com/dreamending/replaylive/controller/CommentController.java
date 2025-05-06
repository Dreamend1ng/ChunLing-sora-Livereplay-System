package com.dreamending.replaylive.controller;

import org.springframework.web.bind.annotation.*;

/*
    @com.dreamending.replaylive.controller
    @Author: Sun Weize - 19393
    @date 2025-04-22  08:05
*/
import com.dreamending.replaylive.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
   @Autowired
   private CommentService commentService;
   @GetMapping("/getComments/{video_id}")
   public List<String> getComments(@PathVariable String video_id) {
       if (video_id == null) {
           String ErrMess = "Err: video_id is null";
           return Collections.singletonList(ErrMess);
       }
       return commentService.getComments(video_id);
   }
   @GetMapping("/getAllComments")
   public List<String> getAllComments() {
       return commentService.getAllComments();
   }
   @PostMapping("/sendComment")
   public void sendComment(String video_id, String user_id, String content) {
       commentService.sendComment(video_id, user_id, content);
   }
    @PostMapping("/deleteComment")
    public void delComment(String comment_id) {
        commentService.delComment(comment_id);
    }
}
