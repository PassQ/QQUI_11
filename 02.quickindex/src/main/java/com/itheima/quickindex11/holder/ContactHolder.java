package com.itheima.quickindex11.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima.quickindex11.R;
import com.itheima.quickindex11.adapter.MyBaseAdapter;
import com.itheima.quickindex11.bean.Contact;

/**
 * Created by Administrator on 2016/12/27.
 */
public class ContactHolder extends BaseHolder<Contact> {

    private TextView tvCatalog;
    private ImageView ivIcon;
    private TextView tvName;

    public ContactHolder(Context context, ViewGroup parent,
                         MyBaseAdapter<Contact> adapter,
                         int position, Contact bean) {
        super(context, parent, adapter, position, bean);
    }


    // 创建列表项布局
    @Override
    public View onCreateView(Context context, ViewGroup parent, int position, Contact bean) {
        View item = LayoutInflater.from(context)
                .inflate(R.layout.item_contact, parent, false);

        tvCatalog = (TextView) item.findViewById(R.id.tv_catalog);
        ivIcon = (ImageView) item.findViewById(R.id.iv_icon);
        tvName = (TextView) item.findViewById(R.id.tv_name);

        return item;
    }

    // 刷新列表项子控件的显示
    @Override
    protected void onRefreshView(Contact bean, int position) {
        // 列表第一项需要显示首字母
        if (position == 0) {
            tvCatalog.setVisibility(View.VISIBLE);
            // 显示首字母
            tvCatalog.setText(bean.firstLetter);

        } else { // 不是列表的第一项
            // 与上一个列表项的首字母进行比较，如果不一样才显示，否则不显示

            // 上一个列表项的javabean
            Contact preBean = super.adapter.getItem(position - 1);

            if (!preBean.firstLetter.equals(bean.firstLetter)) { // 首字母不相同，显示
                tvCatalog.setVisibility(View.VISIBLE);
                // 显示首字母
                tvCatalog.setText(bean.firstLetter);
            } else { // 不显示首字母
                tvCatalog.setVisibility(View.GONE);
            }
        }


        // 显示名字
        tvName.setText(bean.name);



    }
}
