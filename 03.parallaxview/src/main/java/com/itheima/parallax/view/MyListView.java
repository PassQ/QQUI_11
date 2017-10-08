package com.itheima.parallax.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2016/12/30.
 */
public class MyListView extends ListView {

    private ImageView ivHeader;
    private int mHeight;

    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** 设置列表头部图片显示的ImageView */
    public void setImageView(final ImageView ivHeader) {
        this.ivHeader = ivHeader;

        // ViewTreeObserver: 监听视图树的变化
        // （初始化显示，添加子控件，删除子控件，弹出输入法，隐藏输入法等）
        getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                mHeight = ivHeader.getHeight();
                // 获取测量后的高度
                int measureHeight = ivHeader.getMeasuredHeight();
                // 获取图片的高度
                int imageHeight = ivHeader.getDrawable().getIntrinsicHeight();

                System.out.println("----height: " + mHeight);
                System.out.println("----measureHeight: " + measureHeight);
                System.out.println("----imageHeight: " + imageHeight);

                // 删除监听器
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    // 列表滑到头时调用（顶部到头往下拉，或者底部到头往上拉时调用）
    // deltaY： 相对于上一次move事件的偏移量，顶部到头往下拉为负值
    // isTouchEvent： 用户按下滑动时为true，惯性滑动为false
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                   int scrollY, int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

        // System.out.println("-----overScrollBy()");
        // System.out.println("-----deltaY: " + deltaY);
        // System.out.println("-----isTouchEvent: " + isTouchEvent);

        // if (用户按下滑动 && 顶部到头往下拉)
        if (isTouchEvent && deltaY < 0) {
            // 改变头部ImageView的高度
            RelativeLayout.LayoutParams params =
                    (RelativeLayout.LayoutParams) ivHeader.getLayoutParams();

            // ImageView的最大高度为260
            if (params.height < dp2px(260)) {  // 260dp
                int dx = Math.abs(deltaY) / 2;
                // 重新设置ImageView的高度
                params.height = params.height + dx;
                ivHeader.setLayoutParams(params);
            }
        }

        return super.overScrollBy(deltaX, deltaY, scrollX,
                scrollY, scrollRangeX, scrollRangeY,
                maxOverScrollX, maxOverScrollY, isTouchEvent);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // 处理up事件，实现回弹效果
        if (ev.getAction() == MotionEvent.ACTION_UP) {

            // 只有头部高度发生了变化后，才执行回弹动画
            if (ivHeader.getLayoutParams().height != mHeight) {
                // 松开手时实现回弹效果
                animateUp();
            }
        }

        return super.onTouchEvent(ev);
    }

    // 松开手时实现回弹效果
    private void animateUp() {
        final RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams)
                ivHeader.getLayoutParams();

        // 开始值： 松开手时的高度
        final int start = param.height;
        // 结束值： 原始高度
        final int end = mHeight;
        // 百分比
        final float percent = 0;

        // 方式一：
//        ValueAnimator animator = ValueAnimator.ofInt(1);
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float percent = animation.getAnimatedFraction();
//                // 估值器：
//                int height = (int) (start + (end - start) * percent);
//                System.out.println("-----percent: " + percent + "   变化的高度： " + height);
//                param.height = height; // 设置新的高度
//                ivHeader.setLayoutParams(param);
//            }
//        });
//        animator.setDuration(300);
//        animator.setInterpolator(new OvershootInterpolator(4));
//        animator.start();


        // 方式二：
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (int) animation.getAnimatedValue();
                param.height = height; // 设置新的高度
                ivHeader.setLayoutParams(param);
            }
        });
        animator.setDuration(300);
        animator.setInterpolator(new OvershootInterpolator(4));
        animator.start();
    }

    private int dp2px(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }
}










