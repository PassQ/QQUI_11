package com.itheima.quickindex11.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.itheima.quickindex11.bean.Contact;
import com.itheima.quickindex11.holder.BaseHolder;
import com.itheima.quickindex11.holder.ContactHolder;

import java.util.List;

/**
 * Created by Administrator on 2016/12/27.
 */
public class ContactAdapter extends MyBaseAdapter<Contact> {

    public ContactAdapter(Context context, List<Contact> listData) {
        super(context, listData);
    }

    // 创建holder的方法
    @Override
    public BaseHolder<Contact> createViewHolder(Context context,
                                                ViewGroup parent, Contact bean, int position) {
        return new ContactHolder(context, parent, this, position, bean);
    }
}
