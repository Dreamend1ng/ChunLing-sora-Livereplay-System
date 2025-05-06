package com.dreamending.replaylive.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dreamending.replaylive.entity.ReplayVideo;
import com.dreamending.replaylive.mapper.cl_replayMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.List;
import java.util.stream.Collectors;

/*
    @com.dreamending.replaylive.service
    @Author: Sun Weize - 19393
    @date 2025-04-15  09:23
*/
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.EnableScheduling;

@Service
@EnableScheduling
public class ReplayServiceImpl implements ReplayService {
    @Autowired
    private cl_replayMapper replayMapper;

    private static final String RECORD_DIR = "D:\\LiveRecord";
    private static final Pattern FILENAME_PATTERN = Pattern.compile(
            "\\[(.*?)\\]\\[(.*?)\\]\\[(.*?)\\]"
    );

    @Override
    public List<String> getAllReplay() {
        QueryWrapper<ReplayVideo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "live_date", "specialturn", "morning_or_evening", "livetitle");

        List<ReplayVideo> replays = replayMapper.selectList(queryWrapper);
        System.out.println(replays);
        return replays.stream()
                .map(replay -> String.format("ID: %s, LiveDate: %s, SpecialTurn: %s, LiveDuration: %s, LiveTitle: %s",
                        replay.getId(),
                        replay.getLiveDate(),
                        replay.getSpecialTurn(),
                        replay.isMorningOrEvening() ? "白天" : "晚上",
                        replay.getLivetitle()))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getMorningReplay() {
        QueryWrapper<ReplayVideo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "live_date", "specialturn", "morning_or_evening")
                .eq("morning_or_evening", 0);  // 只查询morningorevening=1的记录

        List<ReplayVideo> replays = replayMapper.selectList(queryWrapper);

        return replays.stream()
                .map(replay -> String.format("ID: %s, LiveDate: %s, SpecialTurn: %s, LiveTitle: %s",
                        replay.getId(),
                        replay.getLiveDate(),
                        replay.getSpecialTurn(),
                        replay.getLivetitle()))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getEveningReplay() {
        QueryWrapper<ReplayVideo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "live_date", "specialturn", "morning_or_evening")
                .eq("morning_or_evening", 1);  // 只查询morningorevening=0的记录

        List<ReplayVideo> replays = replayMapper.selectList(queryWrapper);

        return replays.stream()
                .map(replay -> String.format("ID: %s, LiveDate: %s, SpecialTurn: %s, LiveTitle: %s",
                        replay.getId(),
                        replay.getLiveDate(),
                        replay.getSpecialTurn(),
                        replay.getLivetitle()))
                .collect(Collectors.toList());
    }

    @Override
    @Scheduled(initialDelay = 0, fixedRate = 180 * 1000) //测试，180s执行一次
    //@Scheduled(initialDelay = 0, fixedRate = 6 * 60 * 60 * 1000)  // 每次启动应用程序的时候都自动运行一遍，随后每6小时运行一次
    public void AutoUpdateReplay() {
        File dir = new File(RECORD_DIR);
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                String filename = file.getName();
                Matcher matcher = FILENAME_PATTERN.matcher(filename);

                if (matcher.find()) {
                    String dateStr = matcher.group(1);
                    String liveTitle = matcher.group(3);

                    // 解析日期时间
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
                    LocalDateTime localDateTime = LocalDateTime.parse(dateStr, formatter);
                    Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

                    // 判断白天(8:00-18:00)还是晚上
                    boolean morningOrEvening = (localDateTime.getHour() >= 7);

                    // 创建并填充实体
                    QueryWrapper<ReplayVideo> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("live_date", date);
                    Long count = replayMapper.selectCount(queryWrapper);

                    if (count == 0) {  // 只有不存在时才插入
                        ReplayVideo replay = new ReplayVideo();
                        replay.setMorningOrEvening(morningOrEvening);
                        replay.setLiveDate(date);
                        replay.setFilename(filename);
                        replay.setLivetitle(liveTitle);
                        replayMapper.insert(replay);
                    }
                }
            }
        }
    }
    @Override
    public ResponseEntity<Resource> playReplay(String id) {
        // 根据ID查询视频记录
        ReplayVideo replay = replayMapper.selectById(id);
        if (replay == null) {
            return ResponseEntity.notFound().build();
        }

        // 构建视频文件路径
        File videoFile = new File(RECORD_DIR + File.separator + replay.getFilename());
        if (!videoFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        try {
            // 创建文件资源
            Path path = Paths.get(videoFile.getAbsolutePath());
            Resource resource = new UrlResource(path.toUri());

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + replay.getFilename() + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, "video/mp4");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(videoFile.length())
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
