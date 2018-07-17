package com.example.yunshaonote;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

import com.example.yunshaonote.db.Note;
import com.example.yunshaonote.service.DateRemindService;
import com.example.yunshaonote.service.RemindService;
import com.example.yunshaonote.service.WidgetService;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

public class MainActivity extends AppCompatActivity {

    private ImageButton drawerButton;

    private TextView titleView;

    private ImageButton rightButton;

    private FloatingActionButton floatingActionButton;

    private RecyclerView recyclerView;

    private NoteAdapter adapter;

    private DrawerLayout drawerLayout;

    private ItemManageFragment itemManageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerButton = (ImageButton) findViewById(R.id.left_button);
        titleView = (TextView) findViewById(R.id.title_textview);
        rightButton = (ImageButton) findViewById(R.id.right_button);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.float_button);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        itemManageFragment = (ItemManageFragment) getFragmentManager().findFragmentById(R.id.item_manage_fragment);
        itemManageFragment.setCallback(new ItemManageFragment.Callback(){
            public void refreshRemind(){
                Intent intent = new Intent(MainActivity.this, RemindService.class);
                intent.putExtra("frag", 1);
                startService(intent);
            }
        });
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                itemManageFragment.refresh();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                itemManageFragment.refresh();
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        //创建数据库
        LitePal.initialize(this);
        LitePal.getDatabase();
        drawerButton.setBackgroundResource(R.drawable.sort_light);
        drawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        titleView.setText("便签");
        rightButton.setVisibility(View.INVISIBLE);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                intent.putExtra("date", "add");
                startActivity(intent);
            }
        });
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NoteAdapter(DataSupport.order("timeId desc").find(Note.class));
        recyclerView.setAdapter(adapter);
        //启动后台
        Intent intent = new Intent(this, RemindService.class);
        intent.putExtra("frag", 1);
        startService(intent);
        Intent intent1 = new Intent(this, DateRemindService.class);
        startService(intent1);
        Intent intent2 = new Intent(this, WidgetService.class);
        startService(intent2);
    }

    @Override
    public void onResume() {
        //从数据库导入数据
        super.onResume();
        adapter.refreshList(DataSupport.order("timeId desc").find(Note.class));
        adapter.notifyDataSetChanged();
    }

}
