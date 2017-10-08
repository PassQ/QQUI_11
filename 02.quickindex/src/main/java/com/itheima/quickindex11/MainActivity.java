package com.itheima.quickindex11;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.quickindex11.adapter.ContactAdapter;
import com.itheima.quickindex11.bean.Contact;
import com.itheima.quickindex11.view.LetterBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private ListView listView;

    private TextView tvFirstLetter;

    /** 列表显示的数据集合 */
    private ArrayList<Contact> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvFirstLetter = (TextView) findViewById(R.id.tv_first_letter);

        initLetterBar();
        initListView();
    }

    private void initListView() {
        listView = (ListView) findViewById(R.id.list_view);

        // 列表数据创建
        listData = new ArrayList<>();
        for (int i = 0; i < Constant.LIST_DATAS2.length; i++) {
            Contact bean = new Contact(Constant.LIST_DATAS2[i]);
            listData.add(bean);
        }

        // 按拼音进行排序操作
        Collections.sort(listData, new Comparator<Contact>() {
            @Override
            public int compare(Contact o1, Contact o2) {
                // 按拼音升序排列
                return o1.pingying.compareTo(o2.pingying);
            }
        });

        ContactAdapter mAdapter = new ContactAdapter(this, listData);
        listView.setAdapter(mAdapter);
    }


    private void initLetterBar() {
        LetterBar letterBar = (LetterBar) findViewById(R.id.letter_bar);
        letterBar.setFirstLetterTextView(tvFirstLetter);
        letterBar.setOnLetterSelectedListener(new LetterBar.OnLetterSelectedListener() {
            @Override
            public void onLetterSelected(int index, String letter) {
                System.out.println("----选中的字母：" + letter);
                // 关联字母条与ListView控件
                for (int i = 0; i < listData.size(); i ++) {
                    Contact bean = listData.get(i);
                    if (letter.equals(bean.firstLetter)) {
                        int position = i;
                        // 让列表滚动到指定的item
                        listView.setSelection(position);
                        return;
                    }
                }
            }
        });
    }
}
