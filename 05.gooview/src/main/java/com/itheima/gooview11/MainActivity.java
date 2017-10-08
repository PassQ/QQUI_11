package com.itheima.gooview11;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.itheima.gooview11.view.GooView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(new GooView(this));
    }
}
