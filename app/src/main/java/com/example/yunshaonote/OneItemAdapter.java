package com.example.yunshaonote;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.codbking.widget.DatePickDialog;
import com.codbking.widget.OnSureLisener;
import com.codbking.widget.bean.DateType;
import com.dou361.dialogui.DialogUIUtils;
import com.dou361.dialogui.listener.DialogUIListener;
import com.example.yunshaonote.db.NoteItem;
import com.example.yunshaonote.util.FormatUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 允少 on 2017/9/24.
 */

public class OneItemAdapter extends RecyclerView.Adapter<OneItemAdapter.ViewHolder> {

    private List<NoteItem> mNoteItemList = new ArrayList<>();

    private ItemManageFragment context;

    static class ViewHolder extends RecyclerView.ViewHolder {
        Button button;
        TextView content;
        ImageButton deleteButton;

        public ViewHolder(View view) {
            super(view);
            button = (Button) view.findViewById(R.id.oneitem_button);
            content = (TextView) view.findViewById(R.id.oneitem_textview);
            deleteButton = (ImageButton) view.findViewById(R.id.oneitem_imagebutton);
        }

    }

    public OneItemAdapter(List<NoteItem> noteItemList, ItemManageFragment context) {
        mNoteItemList = noteItemList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.oneitem_manage, parent, false);
        OneItemAdapter.ViewHolder holder = new OneItemAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final NoteItem noteItem = mNoteItemList.get(position);
        holder.button.setText(FormatUtil.longToTime(noteItem.getRemindTime()));
        holder.content.setText(noteItem.getItem());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                DatePickDialog dialog = new DatePickDialog(v.getContext());
                dialog.setYearLimt(2);
                dialog.setStartDate(new Date(noteItem.getRemindTime()));
                dialog.setTitle("选择提醒时间");
                dialog.setType(DateType.TYPE_ALL);
                dialog.setMessageFormat("yyyy-MM-dd HH:mm");
                dialog.setOnSureLisener(new OnSureLisener() {
                    @Override
                    public void onSure(Date remindTime) {
                        if (remindTime.getTime() < System.currentTimeMillis()){
                            Toast.makeText(v.getContext(), "不能设置过去的时间！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        noteItem.setRemindTime(remindTime.getTime());
                        noteItem.save();
                        Toast.makeText(v.getContext(), "修改提醒成功！", Toast.LENGTH_SHORT).show();
                        context.refresh();
                    }
                });
                dialog.show();
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                DialogUIUtils.init(v.getContext());
                DialogUIUtils.showAlertHorizontal(v.getContext(), "提示", "是否删除此提醒？", new DialogUIListener(){
                    @Override
                    public void onPositive() {
                        DataSupport.deleteAll(NoteItem.class, "item = ?", noteItem.getItem());
                        Toast.makeText(v.getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                        context.refresh();
                    }

                    @Override
                    public void onNegative() {

                    }
                }).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNoteItemList.size();
    }

}
