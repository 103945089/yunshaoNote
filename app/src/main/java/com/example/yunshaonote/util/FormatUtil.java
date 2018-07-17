package com.example.yunshaonote.util;

import com.example.yunshaonote.db.Note;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by 允少 on 2017/9/14.
 */

public class FormatUtil {

    public static int DATE_TYPE = 1;

    public static int STICKY_TYPE = 2;

    public static final String CLICK_ACTION_1 = "com.example.yunshaonote.action.CLICK1";
    public static final String CLICK_ACTION_2 = "com.example.yunshaonote.action.CLICK2";
    public static final String CLICK_ACTION_3 = "com.example.yunshaonote.action.CLICK3";
    public static final String CLICK_ACTION_4 = "com.example.yunshaonote.action.CLICK4";
    public static final String CLICK_ACTION_5 = "com.example.yunshaonote.action.CLICK5";
    public static final String CLICK_FULL = "com.example.yunshaonote.action.CLICK0";
    public static final String CLICK_NOT = "com.example.yunshaonote.action.CLICK.NOT";

    public static String dateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        return sdf.format(date);
    }

    public static String getEditTime() {
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
        return formatter.format(now);
    }

    public static boolean isRepeat(String s) {
        List<Note> noteList = DataSupport.where("noteType = ?", "1").find(Note.class);
        if (noteList.size() == 0) {
            return false;
        }
        for (Note note : noteList) {
            if (note.getDate().equals(s)) {
                return true;
            }
        }
        return false;
    }

    public static String switchDate(long id) {
        Date date = new Date(id);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String[] weekends = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        int i = cal.get(Calendar.DAY_OF_WEEK) - 1;
        SimpleDateFormat sdf= new SimpleDateFormat("MM-dd");
        String s[] = sdf.format(date).split("-");
        return s[0] + "月" + s[1] + "日" + "  " + weekends[i];
    }

    public static String longToTime(long l) {
        Date date = new Date(l);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(date);
    }

    public static String longToStringDate(long l) {
        Date date = new Date(l);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        return sdf.format(date);
    }

    public static long getTodayZero(){
        Date date = new Date();
        long oneDay = 24 * 60 * 60 * 1000;
        if(date.getTime()%oneDay >= 16 * 60 * 60 * 1000){
            return (date.getTime() - (date.getTime()%oneDay) + 16 * 60 * 60 * 1000);
        }else {
            return (date.getTime() - (date.getTime()%oneDay - 8 * 60 * 60 * 1000));
        }
    }

}
