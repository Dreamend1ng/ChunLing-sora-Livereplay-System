package com.dreamending.replaylive.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


/*
    @com.dreamending.replaylive.entity
    @Author: Sun Weize - 19393
    @date 2025-04-01  13:33
*/

@Data
@TableName("cl_replay")
public class ReplayVideo {
    @TableId(type = IdType.INPUT)
    @TableField(value = "id")
    private String id;
    @TableField(value = "morning_or_evening")
    private boolean MorningOrEvening;
    @TableField(value = "live_date")
    private Date LiveDate;
    @TableField(value = "specialturn")
    private String SpecialTurn;
    @TableField(value = "filename")
    private String Filename;
    @TableField(value = "livetitle")
    private String Livetitle;

    @Override
    public String toString() {
        return "ReplayVideo{" +
                "id='" + id + '\'' +
                ", MorningOrEvening=" + MorningOrEvening +
                ", LiveDate=" + LiveDate +
                ", isSpecialTurn='" + SpecialTurn + '\'' +
                ", filename='" + Filename + '\'' +
                ", livetitle='" + Livetitle + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isMorningOrEvening() {
        return MorningOrEvening;
    }

    public void setMorningOrEvening(boolean morningOrEvening) {
        MorningOrEvening = morningOrEvening;
    }

    public Date getLiveDate() {
        return LiveDate;
    }

    public void setLiveDate(Date liveDate) {
        LiveDate = liveDate;
    }

    public String getSpecialTurn() {
        return SpecialTurn;
    }

    public void setSpecialTurn(String SpecialTurn) {
        this.SpecialTurn = SpecialTurn;
    }
    public String getFilename() {
        return Filename;
    }

    public void setFilename(String Filename) {
        this.Filename = Filename;
    }

    public String getLivetitle() {
        return Livetitle;
    }

    public void setLivetitle(String Livetitle) {
        this.Livetitle = Livetitle;
    }
}
