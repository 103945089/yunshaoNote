package com.example.yunshaonote.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 允少 on 2017/9/19.
 */

public class NoteItem extends DataSupport {

    private long date;

    private String item;

    private long remindTime;

    public long getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(long remindTime) {
        this.remindTime = remindTime;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }



}
