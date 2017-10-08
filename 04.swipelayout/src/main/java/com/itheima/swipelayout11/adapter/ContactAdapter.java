package com.itheima.swipelayout11.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.itheima.swipelayout11.bean.Contact;
import com.itheima.swipelayout11.holder.BaseHolder;
import com.itheima.swipelayout11.holder.ContactHolder;

import java.util.List;

public class ContactAdapter extends MyBaseAdapter<Contact> {

    public ContactAdapter(Context context, List<Contact> listData) {
        super(context, listData);
    }

    @Override
    public BaseHolder<Contact> createViewHolder(
            Context context, ViewGroup parent,
            Contact bean, int position) {

        return new ContactHolder(context, parent, this, position, bean);
    }
}
