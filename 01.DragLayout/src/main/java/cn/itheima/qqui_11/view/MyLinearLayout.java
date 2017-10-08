package cn.itheima.qqui_11.view;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2016/12/27.
 */

public class MyLinearLayout extends LinearLayout {

    private DragLayout dragLayout;

    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDragLayout(DragLayout dragLayout) {
        this.dragLayout = dragLayout;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 如果侧滑菜单是打开的，则拦截触摸事件，禁止主界面列表上下滑动
        if (dragLayout.isOpen()) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // 如果侧滑菜单是打开的，则消费拦截触摸事件, 这样菜单界面的列表就无法上下滑动了
        if (dragLayout.isOpen()) {
            return true;
        }

        return super.onTouchEvent(event);
    }
}
