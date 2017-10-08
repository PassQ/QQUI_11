package com.itheima.swipelayout11.holder;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.swipelayout11.R;
import com.itheima.swipelayout11.adapter.MyBaseAdapter;
import com.itheima.swipelayout11.bean.Contact;
import com.itheima.swipelayout11.view.SwipeLayoutManger;

import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Administrator on 2016/12/30.
 */
public class ContactHolder extends BaseHolder<Contact> {

    private TextView tvSettop;
    private TextView tvDelete;
    private TextView tvName;
    private LinearLayout llContent;

    public ContactHolder(Context context, ViewGroup parent,
                         MyBaseAdapter<Contact> adapter,
                         int position, Contact bean) {
        super(context, parent, adapter, position, bean);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SwipeLayoutManger.getInstance().closeOpenSwipeLayout();

            switch (v.getId()) {
                case R.id.tv_settop: // 置顶
                    // Toast.makeText(context, "置顶: " + ContactHolder.this.bean, Toast.LENGTH_SHORT).show();
                    // 取出打开的那个列表项的bean
                    Contact bean = adapter.getItem(position);
                    bean.setTopTime = System.currentTimeMillis();
                    // 按置顶时间的降序排列
                    Collections.sort(adapter.listData, new Comparator<Contact>() {
                        @Override
                        public int compare(Contact o1, Contact o2) {
                            return -o1.setTopTime.compareTo(o2.setTopTime);
                        }
                    });
                    adapter.notifyDataSetChanged();

                    break;
                case R.id.tv_delete: // 删除
                    Toast.makeText(context, "删除: " + ContactHolder.this.bean, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    // 创建列表项, 查找列表项子控件
    @Override
    public View onCreateView(final Context context, ViewGroup parent) {

        View item = LayoutInflater.from(context).inflate(
                R.layout.item_list, parent, false);

        llContent = (LinearLayout) item.findViewById(R.id.ll_content);
        tvSettop = (TextView) item.findViewById(R.id.tv_settop);
        tvDelete = (TextView) item.findViewById(R.id.tv_delete);
        tvName = (TextView) item.findViewById(R.id.tv_name);

        tvSettop.setOnClickListener(listener);
        tvDelete.setOnClickListener(listener);

        return item;
    }

    // 刷新列表项子控件的显示
    @Override
    protected void onRefreshView(Contact bean, int position) {
        // 显示联系人的姓名
        tvName.setText(bean.name);

        if (bean.setTopTime > 0) { // 有置顶时间，说明需要置顶显示
            llContent.setBackgroundColor(Color.parseColor("#05ff0000"));
        } else { // 不需要置顶显示
            llContent.setBackgroundColor(Color.parseColor("#ffffffff"));
        }
    }

}
















