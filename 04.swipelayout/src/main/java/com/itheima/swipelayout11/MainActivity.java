package com.itheima.swipelayout11;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.itheima.swipelayout11.adapter.ContactAdapter;
import com.itheima.swipelayout11.bean.Contact;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ContactAdapter mAdapter;

    private ArrayList<Contact> listData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setContentView(R.layout.item_list);
        setContentView(R.layout.activity_main);

        // 初始化列表数据
        for (String item : Constant.LIST_DATAS2) {
            Contact bean = new Contact();
            bean.name = item;
            listData.add(bean);
        }

        listView = (ListView) findViewById(R.id.list_view);
        mAdapter = new ContactAdapter(this, listData);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "item : " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
