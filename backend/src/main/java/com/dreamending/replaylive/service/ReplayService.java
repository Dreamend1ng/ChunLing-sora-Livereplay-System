package com.dreamending.replaylive.service;

import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

/*
    @com.dreamending.replaylive.service
    @Author: Sun Weize - 19393
    @date 2025-04-15  09:23
*/public interface ReplayService {
    List<String> getReplay();
    List<String> getAllReplay();
    void AutoUpdateReplay();
    ResponseEntity<Resource> playReplay(String id);
    List<String> getReplayByDate(String date);
    String hideReplay(String id);
    String changeReplay(String id, String livetitle, String specialturn);

}
