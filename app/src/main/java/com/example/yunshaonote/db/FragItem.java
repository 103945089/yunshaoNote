package com.example.yunshaonote.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 允少 on 2017/10/24.
 */

public class FragItem extends DataSupport{

    private long date;

    private String item;

    public void setDate(long date){
        this.date = date;
    }

    public long getDate(){
        return this.date;
    }

    public void setItem(String item){
        this.item = item;
    }

    public String getItem(){
        return this.item;
    }

}
