package com.dreamending.replaylive.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
    public List<String> getReplay() {
        QueryWrapper<ReplayVideo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "live_date", "specialturn", "livetitle")
                    .eq("hidelive", 0);
        List<ReplayVideo> replays = replayMapper.selectList(queryWrapper);
        System.out.println(replays);
        return replays.stream()
                .map(replay -> String.format("ID: %s, LiveDate: %s, SpecialTurn: %s, LiveTitle: %s",
                        replay.getId(),
                        replay.getLiveDate(),
                        replay.getSpecialTurn(),
                        replay.getLivetitle()))
                .collect(Collectors.toList());
    }
    @Override
    public List<String> getAllReplay() {
        QueryWrapper<ReplayVideo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "live_date", "specialturn", "livetitle", "hidelive");

        List<ReplayVideo> replays = replayMapper.selectList(queryWrapper);
        System.out.println(replays);
        return replays.stream()
                .map(replay -> String.format("ID: %s, LiveDate: %s, SpecialTurn: %s, LiveTitle: %s, hidelive: %s",
                        replay.getId(),
                        replay.getLiveDate(),
                        replay.getSpecialTurn(),
                        replay.getLivetitle(),
                        replay.getHidelive()))
                .collect(Collectors.toList());
    }

    @Override
    //@Scheduled(initialDelay = 0, fixedRate = 180 * 1000) //测试，180s执行一次
    @Scheduled(initialDelay = 0, fixedRate = 6 * 60 * 60 * 1000)  // 每次启动应用程序的时候都自动运行一遍，随后每6小时运行一次
    public void AutoUpdateReplay() {
        File dir = new File(RECORD_DIR);
        File[] files = dir.listFiles();
        System.out.println("AutoUpdateReplay: 开始检查有无新视频文件");
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


                    // 创建并填充实体
                    QueryWrapper<ReplayVideo> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("live_date", date);
                    Long count = replayMapper.selectCount(queryWrapper);

                    if (count == 0) {  // 只有不存在时才插入
                        System.out.println("AutoUpdateReplay: 发现新的视频文件，进行更新");
                        ReplayVideo replay = new ReplayVideo();
                        replay.setLiveDate(date);
                        replay.setFilename(filename);
                        replay.setLivetitle(liveTitle);
                        replayMapper.insert(replay);
                        System.out.println("AutoUpdateReplay: 插入新的视频记录: " + replay);
                    }
                }
            }
            System.out.println("AutoUpdateReplay: 完成任务");
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
    @Override
    public List<String> getReplayByDate(String date) {
        QueryWrapper<ReplayVideo> queryWrapper = new QueryWrapper<>();
        // 使用DATE()函数匹配日期部分
        queryWrapper.apply("DATE(live_date) = {0}", date);

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
    public String hideReplay(String id) {
        UpdateWrapper<ReplayVideo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.setSql("hidelive = ABS(hidelive - 1)") // 使用数值翻转
                   .eq("id", id);

        int result = replayMapper.update(null, updateWrapper);
        return result > 0 ? "状态切换成功" : "操作失败";
    }
    @Override
    public String changeReplay(String id, String livetitle, String specialTurn)  {
            // 检查至少有一个参数需要更新
            if ((livetitle == null || livetitle.isEmpty()) &&
                    (specialTurn == null || specialTurn.isEmpty())) {
                return "至少需要提供一个更新参数（直播标题或特殊场次）";
            }

            UpdateWrapper<ReplayVideo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", id);

            // 动态设置更新字段
            if (livetitle != null && !livetitle.isEmpty()) {
                updateWrapper.set("livetitle", livetitle);
            }
            if (specialTurn != null && !specialTurn.isEmpty()) {
                updateWrapper.set("specialturn", specialTurn);
            }

            int result = replayMapper.update(null, updateWrapper);
            return result > 0 ? "修改成功" : "修改失败（可能未找到记录）";
    }
}

