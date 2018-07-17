package com.example.yunshaonote;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.yunshaonote.db.FragItem;
import com.example.yunshaonote.service.DateRemindService;
import com.example.yunshaonote.util.FormatUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by 允少 on 2017/9/17.
 */

public class OneNoteAdapter extends RecyclerView.Adapter<OneNoteAdapter.ViewHolder>{

    private List<String> mItemList;

    private List<FragItem> mFragItemList;

    private long mDate;

    private int mNoteType;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton imageButton;
        TextView itemText;

        public ViewHolder(View view) {
            super(view);
            imageButton = (ImageButton) view.findViewById(R.id.travel_imagebutton);
            itemText = (TextView) view.findViewById(R.id.travel_textview);
        }
    }

    public OneNoteAdapter(List<String> itemList, List<FragItem> fragItemList, long date, int noteType) {
        mItemList = itemList;
        this.mFragItemList = fragItemList;
        this.mDate = date;
        this.mNoteType = noteType;
    }

    public void refreshList(List<String> itemList, List<FragItem> fragItemList) {
        mItemList = itemList;
        mFragItemList = fragItemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_note, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String item = mItemList.get(position);
        boolean frag = false;
        if (mFragItemList.size() != 0){
            for (int i = 0; i < mFragItemList.size(); i++){
                if (mFragItemList.get(i).getItem().equals(item)){
                    frag = true;
                    break;
                }
            }
        }
        if (frag){
            holder.imageButton.setBackgroundResource(R.drawable.right);
            holder.itemText.getPaint().setAntiAlias(true);
            holder.itemText.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.itemText.setText(item);
        }else{
            holder.imageButton.setBackgroundResource(R.drawable.circle);
            holder.itemText.getPaint().setFlags(0);
            holder.itemText.setText(item);
        }
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否已经设置了下划线
                if (holder.itemText.getPaint().getFlags() == 0){
                    holder.imageButton.setBackgroundResource(R.drawable.right);
                    holder.itemText.getPaint().setAntiAlias(true);
                    holder.itemText.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.itemText.setText(item);
                    FragItem fragItem = new FragItem();
                    fragItem.setDate(mDate);
                    fragItem.setItem(item);
                    fragItem.save();
                    if (mNoteType == FormatUtil.DATE_TYPE){
                        Intent intent1 = new Intent(v.getContext(), DateRemindService.class);
                        v.getContext().startService(intent1);
                    }
                } else {
                    holder.imageButton.setBackgroundResource(R.drawable.circle);
                    holder.itemText.getPaint().setFlags(0);
                    holder.itemText.setText(item);
                    DataSupport.deleteAll(FragItem.class, "date = ? and item = ?", String.valueOf(mDate), item);
                    if (mNoteType == FormatUtil.DATE_TYPE){
                        Intent intent1 = new Intent(v.getContext(), DateRemindService.class);
                        v.getContext().startService(intent1);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

}
