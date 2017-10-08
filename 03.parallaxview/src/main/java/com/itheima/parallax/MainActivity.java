package com.itheima.parallax;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.itheima.parallax.view.MyListView;

public class MainActivity extends AppCompatActivity {

    private MyListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View header = View.inflate(this, R.layout.list_header, null);
        ImageView ivHeader = (ImageView) header.findViewById(R.id.iv_header);

        listView = (MyListView) findViewById(R.id.list_view);
        listView.setImageView(ivHeader);

        // 添加列表头部布局
        listView.addHeaderView(header);
        listView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Constant.LIST_DATAS2));
    }
}















