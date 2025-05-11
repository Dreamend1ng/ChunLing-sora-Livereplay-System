package com.dreamending.replaylive.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/*
    @com.dreamending.replaylive.controller
    @Author: Sun Weize - 19393
    @date 2025-04-01  09:19
*/
@RestController
public class HelloController {
    //@GetMapping("/api/hello")
    @RequestMapping(value="/api/hello",method = RequestMethod.GET)
    public String hello(String nickname){
        return "hello world" + "\n" + nickname;
    }
}
