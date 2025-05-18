package com.dreamending.replaylive.controller;
import com.dreamending.replaylive.service.ReplayService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/replay")
/*
    @com.dreamending.replaylive.controller
    @Author: Sun Weize - 19393
    @date 2025-04-15  09:22
*/public class ReplayController {
    @Autowired
    private ReplayService replayService;

    @GetMapping("/getReplay")
    public Object getReplay() {
        return replayService.getReplay();
    }
    @GetMapping("/getAllReplay")
    public Object getAllReplay() {
        return replayService.getAllReplay();
    }
    @GetMapping("/playReplay/{id}")
    public Object playReplay(@PathVariable String id) {
        return replayService.playReplay(id);
    }
    @GetMapping("/getReplayByDate/{date}")
    public List<String> getReplayByDate(@PathVariable String date) {
        return replayService.getReplayByDate(date);
    }
    @GetMapping("/hideReplay/{id}")
    public String hideReplay(@PathVariable String id) {
        return replayService.hideReplay(id);
    }
    @PostMapping("/changeReplay")
    public String changeReplay(@RequestBody JsonNode request) {
        return replayService.changeReplay(
                request.get("id").asText(),
                request.get("livetitle").asText(),
                request.get("specialturn").asText()

        );
    }
}
