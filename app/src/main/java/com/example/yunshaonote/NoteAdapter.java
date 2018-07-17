package com.example.yunshaonote;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yunshaonote.db.Note;
import com.example.yunshaonote.util.FormatUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 允少 on 2017/9/15.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private List<Note> mNoteList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View noteView;
        TextView dateView;
        RecyclerView recyclerView;
        TextView editTimeView;

        public ViewHolder(View view) {
            super(view);
            noteView = view;
            dateView = (TextView) view.findViewById(R.id.note_date_text);
            recyclerView = (RecyclerView) view.findViewById(R.id.note_item_recyclerView);
            editTimeView = (TextView) view.findViewById(R.id.note_last_edit_text);
        }
    }

    public NoteAdapter(List<Note> noteList) {
        mNoteList = noteList;
    }

    public void refreshList(List<Note> noteList) {
        mNoteList = noteList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.noteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Note note = mNoteList.get(position);
                Intent intent = new Intent(v.getContext(), LookNoteActivity.class);
                if (note.getNoteType() == FormatUtil.DATE_TYPE) {
                    intent.putExtra("date", note.getDate());
                } else {
                    intent.putExtra("date", Long.toString(note.getTimeId()));
                }
                v.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Note note = mNoteList.get(position);
        if (note.getNoteType() == FormatUtil.DATE_TYPE) {
            holder.dateView.setVisibility(View.VISIBLE);
            holder.dateView.setText(FormatUtil.switchDate(note.getTimeId()));
        } else {
            holder.dateView.setVisibility(View.GONE);
        }
        String[] content = note.getContent().split("\n");
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < content.length; i++){
            stringList.add(content[i]);
        }
        //禁止滑动
        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.VERTICAL, false){
            @Override
            public boolean canScrollVertically(){
                return false;
            }
        };
        holder.recyclerView.setLayoutManager(layoutManager);
        ListAdapter adapter = new ListAdapter(stringList, note);
        holder.recyclerView.setAdapter(adapter);
        holder.editTimeView.setText(note.getUpdateTime());
    }

    @Override
    public int getItemCount() {
        return mNoteList.size();
    }

}
