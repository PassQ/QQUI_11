package cn.itheima.qqui_11;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cn.itheima.qqui_11.view.DragLayout;
import cn.itheima.qqui_11.view.MyLinearLayout;

public class MainActivity extends AppCompatActivity {

    private ImageView ivHeader;
    private MyLinearLayout myLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 使用AppCompatActivity时去掉标题：
        // supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        ivHeader = (ImageView) findViewById(R.id.iv_header);
//        myLinearLayout = (MyLinearLayout) findViewById(R.id.my_ll);

        initListView();

        DragLayout dragLayout = (DragLayout) findViewById(R.id.drag_layout);
//        myLinearLayout.setDragLayout(dragLayout);

        dragLayout.setOnDragListener(new DragLayout.OnDragListener() {

            @Override
            public void onOpen() {
                showToast("打开");
                ivHeader.setAlpha(0);
            }

            @Override
            public void onClose() {
                showToast("关闭");

                // 抖动头像
                // (1) 渐变动画
                TranslateAnimation animation = new TranslateAnimation(0, 15, 0, 0);
                animation.setDuration(200);
                animation.setRepeatCount(4);
                ivHeader.startAnimation(animation);

                // (2) 属性动画
//                ivHeader.animate()
//                        .setDuration(600)
//                        // 重复执行4次
//                        .setInterpolator(new CycleInterpolator(4))
//                        .translationX(15);
            }

            @Override
            public void onDragging(float percent) {
                System.out.println("----percent: " + percent);
                ivHeader.setAlpha(1-percent);
            }
        });
    }

    private void initListView() {
        ListView lvMenu = (ListView) findViewById(R.id.lv_menu);
        ListView lvMain = (ListView) findViewById(R.id.lv_main);

        lvMenu.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Constant.MENUS){

            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(14);   // 14sp
                return textView;
            }
        });

        lvMain.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Constant.LIST_DATAS));
    }


    private Toast mToast;

    public void showToast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.show();
    }
}















