package com.example.yunshaonote;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yunshaonote.db.NoteItem;
import com.example.yunshaonote.util.FormatUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 允少 on 2017/9/23.
 */

public class ItemManageAdapter extends RecyclerView.Adapter<ItemManageAdapter.ViewHolder> {

    private List<String> mDateList;

    private OneItemAdapter adapter;

    private List<NoteItem> noteItemList;

    private ItemManageFragment context;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        RecyclerView recyclerView;

        public ViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.item_date_textview);
            recyclerView = (RecyclerView) view.findViewById(R.id.item_date_recyclerview);
        }

    }

    public ItemManageAdapter(List<String> dateList, ItemManageFragment context) {
        mDateList = dateList;
        this.context = context;
    }

    public void refreshList(List<String> dateList) {
        mDateList = dateList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_manage, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String date = mDateList.get(position);
        noteItemList = getNoteItemList(date);
        holder.textView.setText(FormatUtil.switchDate(noteItemList.get(0).getDate()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.itemView.getContext());
        holder.recyclerView.setLayoutManager(layoutManager);
        adapter = new OneItemAdapter(noteItemList, context);
        holder.recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return mDateList.size();
    }

    private List<NoteItem> getNoteItemList(String date) {
        List<NoteItem> list = DataSupport.order("date asc").find(NoteItem.class);
        List<NoteItem> noteItemList = new ArrayList<>();
        for (NoteItem noteItem : list) {
            if (date.equals(FormatUtil.longToStringDate(noteItem.getDate()))) {
                noteItemList.add(noteItem);
            }
        }
        return noteItemList;
    }

}
