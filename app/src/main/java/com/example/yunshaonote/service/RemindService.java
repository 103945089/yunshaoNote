package com.example.yunshaonote.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.example.yunshaonote.LookNoteActivity;
import com.example.yunshaonote.R;
import com.example.yunshaonote.db.NoteItem;
import com.example.yunshaonote.util.FormatUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RemindService extends Service {

    private final int REFRESH_REMIND = 1;
    private final int SEND_REMIND = 2;

    private List<NoteItem> mNoteItemList;

    private AlarmManager manager;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mNoteItemList == null){
            mNoteItemList = new ArrayList<>();
        }
        //获取flag
        int frag = intent.getIntExtra("frag", SEND_REMIND);
        if (frag == REFRESH_REMIND){
            refreshNotification();
        }else if (frag == SEND_REMIND){
            mNoteItemList = DataSupport.order("remindTime asc").find(NoteItem.class);
            if (mNoteItemList.size() != 0){
                NoteItem noteItem = mNoteItemList.get(0);
                Intent intent1 = new Intent(RemindService.this, LookNoteActivity.class);
                intent1.putExtra("date", FormatUtil.dateToString(new Date(noteItem.getDate())));
                PendingIntent pi = PendingIntent.getActivity(RemindService.this, 0, intent1, 0);
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification notification = new NotificationCompat.Builder(RemindService.this).setContentTitle(FormatUtil.longToTime(noteItem.getRemindTime()) + " 提醒：").setContentText(noteItem.getItem()).setWhen(System.currentTimeMillis()).setSmallIcon(R.drawable.bianqian).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.bianqian)).setContentIntent(pi).build();
                notificationManager.notify(2, notification);
                DataSupport.deleteAll(NoteItem.class, "remindTime = ?", Long.toString(noteItem.getRemindTime()));
                refreshNotification();
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void refreshNotification(){
        mNoteItemList = DataSupport.order("remindTime asc").find(NoteItem.class);
        if (mNoteItemList.size() != 0){
            Intent i = new Intent(RemindService.this, RemindService.class);
            PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
            manager = (AlarmManager) getSystemService(ALARM_SERVICE);
            manager.cancel(pi);
            //获取当前系统的版本号
            if (android.os.Build.VERSION.SDK_INT >= 19){
                manager.setExact(AlarmManager.RTC_WAKEUP, mNoteItemList.get(0).getRemindTime(), pi);
            }else {
                manager.set(AlarmManager.RTC_WAKEUP, mNoteItemList.get(0).getRemindTime(), pi);
            }
        }
    }

}
