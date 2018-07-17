package com.example.yunshaonote;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.example.yunshaonote.service.WidgetService;
import com.example.yunshaonote.util.FormatUtil;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class NoteAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.note_app_widget);
        final ComponentName serviceName = new ComponentName(context, WidgetService.class);
        if (WidgetService.itemList != null){
            views.setTextViewText(R.id.widget_title, FormatUtil.switchDate(WidgetService.timeId));
            views.setViewVisibility(R.id.widget_linear, View.VISIBLE);
            Intent i = new Intent(FormatUtil.CLICK_FULL);
            i.setComponent(serviceName);
            PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
            views.setOnClickPendingIntent(R.id.widget_linearlayout, pi);
            views.setViewVisibility(R.id.no_item_textview, View.GONE);
            if (WidgetService.itemList.size() >= 1){
                views.setViewVisibility(R.id.widget_imageView_1, View.VISIBLE);
                views.setViewVisibility(R.id.widget_textView_1, View.VISIBLE);
                views.setImageViewResource(R.id.widget_imageView_1, R.drawable.finish_prompt);
                views.setTextViewText(R.id.widget_textView_1, WidgetService.itemList.get(0));
                Intent preIntent = new Intent(FormatUtil.CLICK_ACTION_1);
                preIntent.setComponent(serviceName);
                PendingIntent pendingIntent = PendingIntent.getService(context, 0, preIntent, 0);
                views.setOnClickPendingIntent(R.id.widget_imageView_1, pendingIntent);
            }else {
                views.setViewVisibility(R.id.widget_imageView_1, View.GONE);
                views.setViewVisibility(R.id.widget_textView_1, View.GONE);
            }
            if (WidgetService.itemList.size() >= 2){
                views.setViewVisibility(R.id.widget_imageView_2, View.VISIBLE);
                views.setViewVisibility(R.id.widget_textView_2, View.VISIBLE);
                views.setImageViewResource(R.id.widget_imageView_2, R.drawable.finish_prompt);
                views.setTextViewText(R.id.widget_textView_2, WidgetService.itemList.get(1));
                Intent preIntent = new Intent(FormatUtil.CLICK_ACTION_2);
                preIntent.setComponent(serviceName);
                PendingIntent pendingIntent = PendingIntent.getService(context, 0, preIntent, 0);
                views.setOnClickPendingIntent(R.id.widget_imageView_2, pendingIntent);
            }else{
                views.setViewVisibility(R.id.widget_imageView_2, View.GONE);
                views.setViewVisibility(R.id.widget_textView_2, View.GONE);
            }
            if (WidgetService.itemList.size() >= 3){
                views.setViewVisibility(R.id.widget_imageView_3, View.VISIBLE);
                views.setViewVisibility(R.id.widget_textView_3, View.VISIBLE);
                views.setImageViewResource(R.id.widget_imageView_3, R.drawable.finish_prompt);
                views.setTextViewText(R.id.widget_textView_3, WidgetService.itemList.get(2));
                Intent preIntent = new Intent(FormatUtil.CLICK_ACTION_3);
                preIntent.setComponent(serviceName);
                PendingIntent pendingIntent = PendingIntent.getService(context, 0, preIntent, 0);
                views.setOnClickPendingIntent(R.id.widget_imageView_3, pendingIntent);
            }else {
                views.setViewVisibility(R.id.widget_imageView_3, View.GONE);
                views.setViewVisibility(R.id.widget_textView_3, View.GONE);
            }
            if (WidgetService.itemList.size() >= 4){
                views.setViewVisibility(R.id.widget_imageView_4, View.VISIBLE);
                views.setViewVisibility(R.id.widget_textView_4, View.VISIBLE);
                views.setImageViewResource(R.id.widget_imageView_4, R.drawable.finish_prompt);
                views.setTextViewText(R.id.widget_textView_4, WidgetService.itemList.get(3));
                Intent preIntent = new Intent(FormatUtil.CLICK_ACTION_4);
                preIntent.setComponent(serviceName);
                PendingIntent pendingIntent = PendingIntent.getService(context, 0, preIntent, 0);
                views.setOnClickPendingIntent(R.id.widget_imageView_4, pendingIntent);
            }else {
                views.setViewVisibility(R.id.widget_imageView_4, View.GONE);
                views.setViewVisibility(R.id.widget_textView_4, View.GONE);
            }
            if (WidgetService.itemList.size() >= 5){
                views.setViewVisibility(R.id.widget_imageView_5, View.VISIBLE);
                views.setViewVisibility(R.id.widget_textView_5, View.VISIBLE);
                views.setImageViewResource(R.id.widget_imageView_5, R.drawable.finish_prompt);
                views.setTextViewText(R.id.widget_textView_5, WidgetService.itemList.get(4));
                Intent preIntent = new Intent(FormatUtil.CLICK_ACTION_5);
                preIntent.setComponent(serviceName);
                PendingIntent pendingIntent = PendingIntent.getService(context, 0, preIntent, 0);
                views.setOnClickPendingIntent(R.id.widget_imageView_5, pendingIntent);
            }else {
                views.setViewVisibility(R.id.widget_imageView_5, View.GONE);
                views.setViewVisibility(R.id.widget_textView_5, View.GONE);
            }
        }else{
            views.setViewVisibility(R.id.widget_linear, View.GONE);
            views.setViewVisibility(R.id.no_item_textview, View.VISIBLE);
            Intent preIntent = new Intent(FormatUtil.CLICK_NOT);
            preIntent.setComponent(serviceName);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, preIntent, 0);
            views.setOnClickPendingIntent(R.id.widget_linearlayout, pendingIntent);
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        WidgetService.sAppWidgetIds = appWidgetIds;
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        super.onDisabled(context);
    }

    //对外提供的更新所有小工具的界面接口
    public static void performUpdates(Context context, List<String> itemList, long timeId) {
        WidgetService.itemList = itemList;
        WidgetService.timeId = timeId;
        //如果没有小工具的id，就没法更新界面
        if(WidgetService.sAppWidgetIds == null || WidgetService.sAppWidgetIds.length == 0) {
            return;
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        //遍历每个桌面上的小工具，根据id逐个更新界面
        for (int appWidgetId : WidgetService.sAppWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

}

