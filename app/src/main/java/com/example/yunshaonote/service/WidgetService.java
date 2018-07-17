package com.example.yunshaonote.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.example.yunshaonote.LookNoteActivity;
import com.example.yunshaonote.NoteAppWidget;
import com.example.yunshaonote.db.FragItem;
import com.example.yunshaonote.util.FormatUtil;

import java.util.List;

public class WidgetService extends Service {

    public static List<String> itemList;
    public static long timeId;

    public static int[] sAppWidgetIds;

    public WidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (FormatUtil.CLICK_FULL.equals(action)) {
            Intent i = new Intent(this, LookNoteActivity.class);
            i.putExtra("date", FormatUtil.longToStringDate(timeId));
            startActivity(i);
        }else if (FormatUtil.CLICK_NOT.equals(action)) {
            Toast.makeText(this, "无今日日程", Toast.LENGTH_SHORT).show();
        }else {
            boolean isRefresh = false;
            if (FormatUtil.CLICK_ACTION_1.equals(action)){
                FragItem fragItem = new FragItem();
                fragItem.setDate(timeId);
                fragItem.setItem(itemList.get(0));
                fragItem.save();
                isRefresh = true;
            }else if (FormatUtil.CLICK_ACTION_2.equals(action)){
                FragItem fragItem = new FragItem();
                fragItem.setDate(timeId);
                fragItem.setItem(itemList.get(1));
                fragItem.save();
                isRefresh = true;
            }else if (FormatUtil.CLICK_ACTION_3.equals(action)){
                FragItem fragItem = new FragItem();
                fragItem.setDate(timeId);
                fragItem.setItem(itemList.get(2));
                fragItem.save();
                isRefresh = true;
            }else if (FormatUtil.CLICK_ACTION_4.equals(action)){
                FragItem fragItem = new FragItem();
                fragItem.setDate(timeId);
                fragItem.setItem(itemList.get(3));
                fragItem.save();
                isRefresh = true;
            }else if (FormatUtil.CLICK_ACTION_5.equals(action)){
                FragItem fragItem = new FragItem();
                fragItem.setDate(timeId);
                fragItem.setItem(itemList.get(4));
                fragItem.save();
                isRefresh = true;
            }
            if (isRefresh){
                Intent intent1 = new Intent(this, DateRemindService.class);
                startService(intent1);
                Intent intent2 = new Intent(this, RemindService.class);
                intent2.putExtra("frag", 1);
                startService(intent2);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

}
