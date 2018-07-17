package com.example.yunshaonote;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codbking.widget.DatePickDialog;
import com.codbking.widget.OnSureLisener;
import com.codbking.widget.bean.DateType;
import com.dou361.dialogui.DialogUIUtils;
import com.dou361.dialogui.listener.DialogUIListener;
import com.example.yunshaonote.db.FragItem;
import com.example.yunshaonote.db.Note;
import com.example.yunshaonote.db.NoteItem;
import com.example.yunshaonote.service.DateRemindService;
import com.example.yunshaonote.service.RemindService;
import com.example.yunshaonote.util.FormatUtil;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.support.v7.widget.ListPopupWindow.MATCH_PARENT;

public class LookNoteActivity extends AppCompatActivity {

    private String date;

    private ImageView leftButton;

    private TextView title;

    private ImageView rightButton;

    private Note note;

    private NoteItem noteItem;

    private String content;

    private String[] itemArr;

    private List<String> itemList;

    private OneNoteAdapter adapter;

    private FloatingActionButton deleteButton;

    private SwipeMenuRecyclerView swipeRecyclerView;

    private ImageView picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_note);
        date = getIntent().getStringExtra("date");
        List<Note> noteList = DataSupport.where("date = ?", date).find(Note.class);
        if (noteList.size() == 0) {
            noteList = DataSupport.where("timeId = ?", date).find(Note.class);
        }
        note = noteList.get(0);
        content = note.getContent();
        itemArr = content.split("\n");
        itemList = Arrays.asList(itemArr);
        leftButton = (ImageButton) findViewById(R.id.left_button);
        leftButton.setBackgroundResource(R.drawable.back_light);
        title = (TextView) findViewById(R.id.title_textview);
        //编辑按钮
        rightButton = (ImageButton) findViewById(R.id.right_button);
        deleteButton = (FloatingActionButton) findViewById(R.id.delete_float_button);
        picture = (ImageView) findViewById(R.id.look_picture);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (note.getNoteType() == FormatUtil.DATE_TYPE) {
            title.setText(FormatUtil.switchDate(note.getTimeId()));
        } else {
            title.setText("记事本");
        }
        rightButton.setBackgroundResource(R.drawable.edit);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LookNoteActivity.this, EditNoteActivity.class);
                intent.putExtra("date", date);
                startActivity(intent);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUIUtils.init(LookNoteActivity.this);
                DialogUIUtils.showAlertHorizontal(LookNoteActivity.this, "提示", "是否删除此日程？", new DialogUIListener(){
                    @Override
                    public void onPositive() {
                        if (note.getNoteType() == FormatUtil.DATE_TYPE) {
                            DataSupport.deleteAll(Note.class, "date = ?", note.getDate());
                            DataSupport.deleteAll(NoteItem.class, "date = ?", String.valueOf(note.getTimeId()));
                            Intent intent = new Intent(LookNoteActivity.this, RemindService.class);
                            intent.putExtra("frag", 1);
                            startService(intent);
                            Intent intent1 = new Intent(LookNoteActivity.this, DateRemindService.class);
                            startService(intent1);
                        } else {
                            DataSupport.deleteAll(Note.class, "updateTime = ?", note.getUpdateTime());
                        }
                        Toast.makeText(LookNoteActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onNegative() {

                    }
                }).show();
            }
        });
        swipeRecyclerView = (SwipeMenuRecyclerView) findViewById(R.id.swipe_menu_recycler);
        swipeRecyclerView.setSwipeMenuCreator(new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
                SwipeMenuItem remindItem = new SwipeMenuItem(getApplicationContext()).setBackgroundColor(Color.GREEN).setText("提醒").setTextColor(Color.WHITE).setTextSize(15).setWidth(100).setHeight(MATCH_PARENT);
                swipeRightMenu.addMenuItem(remindItem);
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext()).setBackgroundColor(Color.RED).setText("删除").setTextColor(Color.WHITE).setTextSize(15).setWidth(100).setHeight(MATCH_PARENT);
                swipeRightMenu.addMenuItem(deleteItem);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        swipeRecyclerView.addItemDecoration(new DefaultItemDecoration(Color.GRAY));
        swipeRecyclerView.setLayoutManager(layoutManager);
        swipeRecyclerView.setSwipeMenuItemClickListener(new SwipeMenuItemClickListener() {
            @Override
            public void onItemClick(final SwipeMenuBridge menuBridge) {
                menuBridge.closeMenu();
                if (menuBridge.getPosition() == 0 && note.getNoteType() == FormatUtil.DATE_TYPE) {
                    if(note.getTimeId() < FormatUtil.getTodayZero()){
                        DialogUIUtils.init(LookNoteActivity.this);
                        DialogUIUtils.showAlertHorizontal(LookNoteActivity.this, "提示", "已过去的日程不能设置提醒！", new DialogUIListener() {
                            @Override
                            public void onPositive() {

                            }

                            @Override
                            public void onNegative() {

                            }
                        }).show();
                    }else {
                        List<NoteItem> noteItemList = DataSupport.where("date = ?", String.valueOf(note.getTimeId())).where("item = ?", itemList.get(menuBridge.getAdapterPosition())).find(NoteItem.class);
                        if (noteItemList.size() == 0) {
                            DatePickDialog dialog = new DatePickDialog(LookNoteActivity.this);
                            dialog.setStartDate(new Date(note.getTimeId()));
                            dialog.setYearLimt(2);
                            dialog.setTitle("选择添加的提醒的提醒时间");
                            dialog.setType(DateType.TYPE_HM);
                            dialog.setMessageFormat("HH:mm");
                            dialog.setOnSureLisener(new OnSureLisener() {
                                @Override
                                public void onSure(Date remindTime) {
                                    if (remindTime.getTime() < System.currentTimeMillis()){
                                        Toast.makeText(LookNoteActivity.this, "不能设置已过去的时间！", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    noteItem = new NoteItem();
                                    noteItem.setDate(note.getTimeId());
                                    noteItem.setItem(itemList.get(menuBridge.getAdapterPosition()));
                                    noteItem.setRemindTime(remindTime.getTime());
                                    noteItem.save();
                                    Intent intent = new Intent(LookNoteActivity.this, RemindService.class);
                                    intent.putExtra("frag", 1);
                                    startService(intent);
                                    Toast.makeText(LookNoteActivity.this, "设置提醒成功!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            dialog.show();
                        } else if (noteItemList.size() == 1) {
                            noteItem = noteItemList.get(0);
                            DatePickDialog dialog = new DatePickDialog(LookNoteActivity.this);
                            dialog.setStartDate(new Date(noteItem.getRemindTime()));
                            dialog.setYearLimt(2);
                            dialog.setTitle("选择修改后的提醒时间");
                            dialog.setType(DateType.TYPE_HM);
                            dialog.setMessageFormat("HH:mm");
                            dialog.setOnSureLisener(new OnSureLisener() {
                                @Override
                                public void onSure(Date remindTime) {
                                    if (remindTime.getTime() < System.currentTimeMillis()){
                                        Toast.makeText(LookNoteActivity.this, "不能设置已过去的时间！", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    noteItem.setItem(itemList.get(menuBridge.getAdapterPosition()));
                                    noteItem.setRemindTime(remindTime.getTime());
                                    noteItem.updateAll();
                                    Intent intent = new Intent(LookNoteActivity.this, RemindService.class);
                                    intent.putExtra("frag", 1);
                                    startService(intent);
                                    Toast.makeText(LookNoteActivity.this, "修改提醒成功!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            dialog.show();
                        } else {
                            Toast.makeText(LookNoteActivity.this, "打开提醒设置失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (menuBridge.getPosition() == 1){
                    DialogUIUtils.init(LookNoteActivity.this);
                    DialogUIUtils.showAlertHorizontal(LookNoteActivity.this, "提示", "是否删除该事项？", new DialogUIListener(){
                        @Override
                        public void onPositive() {
                            String deleteItem = itemList.get(menuBridge.getAdapterPosition());
                            List<FragItem> fragItemList = DataSupport.where("date = ?", String.valueOf(note.getTimeId())).where("item = ?", deleteItem).find(FragItem.class);
                            if (fragItemList.size() == 1){
                                DataSupport.deleteAll(FragItem.class, "date = ? and item = ?", String.valueOf(note.getTimeId()), deleteItem);
                            }
                            itemList = new ArrayList<String>(itemList);
                            itemList.remove(menuBridge.getAdapterPosition());
                            String sum = "";
                            for (String s : itemList){
                                sum = sum + "\n" + s;
                            }
                            sum = sum.substring(2, sum.length());
                            note.setContent(sum);
                            if (note.getNoteType() == FormatUtil.DATE_TYPE) {
                                note.updateAll("date = ?", note.getDate());
                                DataSupport.deleteAll(NoteItem.class, "date = ? and item = ?", String.valueOf(note.getTimeId()), deleteItem);
                                Intent intent = new Intent(LookNoteActivity.this, RemindService.class);
                                intent.putExtra("frag", 1);
                                startService(intent);
                                Intent intent1 = new Intent(LookNoteActivity.this, DateRemindService.class);
                                startService(intent1);
                            } else {
                                note.updateAll("timeId = ?", Long.toString(note.getTimeId()));
                            }
                            refreshData();
                            Toast.makeText(LookNoteActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNegative() {

                        }
                    }).show();
                }else {
                    DialogUIUtils.init(LookNoteActivity.this);
                    DialogUIUtils.showAlertHorizontal(LookNoteActivity.this, "提示", "未设置日期的便签不能设置提醒！", new DialogUIListener() {
                        @Override
                        public void onPositive() {

                        }

                        @Override
                        public void onNegative() {

                        }
                    }).show();
                }
            }
        });
        List<FragItem> fragItemList = DataSupport.where("date = ?", String.valueOf(note.getTimeId())).find(FragItem.class);
        adapter = new OneNoteAdapter(itemList, fragItemList, note.getTimeId(), note.getNoteType());
        swipeRecyclerView.setAdapter(adapter);
    }

    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData(){
        //从数据库更新数据
        List<Note> noteList = new ArrayList<>();
        if (note.getNoteType() == FormatUtil.DATE_TYPE) {
            noteList = DataSupport.where("date = ?", date).find(Note.class);
            note = noteList.get(0);
            title.setText(FormatUtil.switchDate(note.getTimeId()));
        } else {
            noteList = DataSupport.where("timeId = ?", date).find(Note.class);
            note = noteList.get(0);
            title.setText("记事本");
        }
        content = note.getContent();
        itemArr = content.split("\n");
        itemList = Arrays.asList(itemArr);
        List<FragItem> fragItemList = DataSupport.where("date = ?", String.valueOf(note.getTimeId())).find(FragItem.class);
        adapter.refreshList(itemList, fragItemList);
        adapter.notifyDataSetChanged();
        if (note.getImagePath() != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(note.getImagePath());
            picture.setImageBitmap(bitmap);
        }else {
            picture.setImageBitmap(null);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

}
