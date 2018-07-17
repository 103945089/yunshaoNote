package com.example.yunshaonote;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.yunshaonote.db.NoteItem;
import com.example.yunshaonote.util.FormatUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 允少 on 2017/9/23.
 */

public class ItemManageFragment extends Fragment {

    private Button backButton;

    private RecyclerView recyclerView;

    private List<String> dateList = new ArrayList<>();

    private DrawerLayout drawerLayout;

    private ItemManageAdapter adapter;

    public Callback callback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_manage, container, false);
        backButton = (Button) view.findViewById(R.id.left_itemmanage_button);
        backButton.setBackgroundResource(R.drawable.back_light);
        recyclerView = (RecyclerView) view.findViewById(R.id.manage_recyclerview);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ItemManageAdapter(dateList, ItemManageFragment.this);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void refresh() {
        callback.refreshRemind();
        //从数据库刷新数据
        getDateList();
        adapter.refreshList(dateList);
        adapter.notifyDataSetChanged();
    }

    //从数据库中获取数据
    private void getDateList() {
        dateList.clear();
        List<NoteItem> noteItemList = DataSupport.findAll(NoteItem.class);
        String mark = "";
        for (NoteItem noteItem : noteItemList) {
            String itemDate = FormatUtil.longToStringDate(noteItem.getDate());
            if (itemDate.equals(mark)) {

            } else {
                dateList.add(itemDate);
                mark = itemDate;
            }
        }
    }

    public interface Callback{

        public void refreshRemind();

    }

    public void setCallback(Callback callback){
        this.callback = callback;
    }

}
