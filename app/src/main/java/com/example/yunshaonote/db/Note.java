package com.example.yunshaonote.db;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 允少 on 2017/9/12.
 */

public class Note extends DataSupport {

    //1：有日期
    //2：无日期
    private int noteType;

    //供item检索使用
    private long timeId;

    private String date;

    private String content;

    private String updateTime;

    private String imagePath;

    public int getNoteType() {
        return noteType;
    }

    public void setNoteType(int noteType) {
        this.noteType = noteType;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setTimeId(long id) {
        this.timeId = id;
    }

    public long getTimeId() {
        return timeId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

}
