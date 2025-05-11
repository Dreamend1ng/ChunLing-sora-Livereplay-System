package com.dreamending.replaylive.controller;
import com.dreamending.replaylive.service.ReplayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/replay")
/*
    @com.dreamending.replaylive.controller
    @Author: Sun Weize - 19393
    @date 2025-04-15  09:22
*/public class ReplayController {
    @Autowired
    private ReplayService replayService;
    @GetMapping("/getAllReplay")
    public Object getAllReplay() {
        return replayService.getAllReplay();
    }
    @GetMapping("/getEveningReplay")
    public Object getEveningReplay() {
        return replayService.getEveningReplay();
    }
    @GetMapping("/getMorningReplay")
    public Object getMorningReplay() {
        return replayService.getMorningReplay();
    }
    @GetMapping("/playReplay/{id}")
    public Object playReplay(@PathVariable String id) {
        return replayService.playReplay(id);
    }
    @GetMapping("/playReplay_NoVerify/{id}")
    public Object playReplay_NoVerify(@PathVariable String id) {
        return replayService.playReplay(id);
    }
}
