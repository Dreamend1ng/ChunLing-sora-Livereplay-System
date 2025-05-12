package com.dreamending.replaylive.controller;

import com.fasterxml.jackson.databind.JsonNode;
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
   public void sendComment( @RequestBody JsonNode request) {
       commentService.sendComment(
               request.get("video_id").asText(),
               request.get("user_id").asText(),
               request.get("content").asText()
       );
   }

    @PostMapping("/deleteComment")
    public void delComment( @RequestBody JsonNode request) {
        commentService.delComment(
                request.get("comment_id").asText()
        );
    }
}
