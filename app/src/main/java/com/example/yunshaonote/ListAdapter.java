package com.example.yunshaonote;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yunshaonote.db.FragItem;
import com.example.yunshaonote.db.Note;
import com.example.yunshaonote.util.FormatUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by 允少 on 2017/10/26.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<String> mItemList;

    private Note mNote;

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView;

        View itemView;

        public ViewHolder(View view){
            super(view);
            textView = (TextView) view.findViewById(R.id.list_item_textView);
            itemView = view;
        }

    }

    public ListAdapter(List<String> itemList, Note note){
        mItemList = itemList;
        mNote = note;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LookNoteActivity.class);
                if (mNote.getNoteType() == FormatUtil.DATE_TYPE) {
                    intent.putExtra("date", mNote.getDate());
                } else {
                    intent.putExtra("date", Long.toString(mNote.getTimeId()));
                }
                v.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        String item = mItemList.get(position);
        List<FragItem> fragItemList = DataSupport.where("date = ?", String.valueOf(mNote.getTimeId())).where("item = ?", item).find(FragItem.class);
        if (fragItemList.size() == 1){
            holder.textView.getPaint().setAntiAlias(true);
            holder.textView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }else{
            holder.textView.getPaint().setFlags(0);
        }
        holder.textView.setText(item);
    }

    @Override
    public int getItemCount(){
        return mItemList.size();
    }

}
