package com.example.yunshaonote.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.example.yunshaonote.LookNoteActivity;
import com.example.yunshaonote.NoteAppWidget;
import com.example.yunshaonote.R;
import com.example.yunshaonote.db.FragItem;
import com.example.yunshaonote.db.Note;
import com.example.yunshaonote.util.FormatUtil;

import org.litepal.crud.DataSupport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateRemindService extends Service {

    private AlarmManager manager;

    private Notification notification;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateDateRemindNote();
        manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int oneHour = 1 * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + oneHour;
        Intent i = new Intent(this, DateRemindService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    public void updateDateRemindNote() {
        stopForeground(true);
        //获取系统时间
        Date now = new Date(FormatUtil.getTodayZero());
        DateFormat format = new SimpleDateFormat("MM-dd");
        String nowString = format.format(now);
        List<Note> noteList = DataSupport.where("date = ?", nowString).find(Note.class);
        if (noteList.size() != 0) {
            Note note = noteList.get(0);
            String[] contentArr = note.getContent().split("\n");
            List<String> itemList = new ArrayList<>();
            List<FragItem> fragItemList = DataSupport.where("date = ?", String.valueOf(note.getTimeId())).find(FragItem.class);
            for (String item : contentArr){
                boolean frag = true;
                for (int i = 0; i < fragItemList.size(); i++){
                    if (item.equals(fragItemList.get(i).getItem())){
                        frag = false;
                        break;
                    }
                }
                if (frag){
                    itemList.add(item);
                }
            }
            String content = "";
            for (String item : itemList){
                content = content + "\n" + item;
            }
            if (content.length() == 0){
                stopForeground(false);
                updateAppWidget(null);
            }else {
                content.substring(2, content.length());
                Intent intent = new Intent(DateRemindService.this, LookNoteActivity.class);
                intent.putExtra("date", note.getDate());
                PendingIntent pi = PendingIntent.getActivity(DateRemindService.this, 0, intent, 0);
                notification = new NotificationCompat.Builder(DateRemindService.this).setContentTitle(FormatUtil.switchDate(System.currentTimeMillis())).setContentText(content).setSmallIcon(R.mipmap.banbantang).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.banbantang)).setWhen(System.currentTimeMillis()).setContentIntent(pi).build();
                startForeground(1, notification);
                updateAppWidget(note);
            }
        }else{
            stopForeground(false);
            updateAppWidget(null);
        }
    }

    public void updateAppWidget(Note note){
        if (note != null){
            String[] itemArr = note.getContent().split("\n");
            List<String> itemList = new ArrayList<>();
            List<FragItem> fragItemList = DataSupport.where("date = ?", String.valueOf(note.getTimeId())).find(FragItem.class);
            for (String item : itemArr){
                boolean frag = true;
                for (int i = 0; i < fragItemList.size(); i++){
                    if (item.equals(fragItemList.get(i).getItem())){
                        frag = false;
                        break;
                    }
                }
                if (frag){
                    itemList.add(item);
                }
            }
            NoteAppWidget.performUpdates(DateRemindService.this, itemList, note.getTimeId());
        }else {
            NoteAppWidget.performUpdates(DateRemindService.this, null, 0);
        }
    }

}
