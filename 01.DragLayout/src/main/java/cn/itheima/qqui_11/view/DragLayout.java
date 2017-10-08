package cn.itheima.qqui_11.view;

import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2016/12/27.
 */
public class DragLayout extends FrameLayout {

    /** 菜单界面 */
    private View menuView;

    /** 主界面 */
    private View mainView;

    private ViewDragHelper dragHelper;

    /** DragLayout的宽 */
    private int mWidth;

    /** DragLayout的高 */
    private int mHeight;

    public DragLayout(Context context) {
        super(context);
        init();
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /** 水平方向拖动的最大范围 */
    private int mRange;

    // 4. 处理Callback回调方法
    ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {

        // (1) 是捕获子控件，返回true表示捕获，捕获到之后子控件就可以滑动了
        // child: 用户滑动的子界面
        // Capture: 捕获
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        // (2) 设置子界面将要显示的位置, 是以child的左边界(left)来确定水平方向的位置的
        // child： 被拖动的子控件
        // left： 被拖动的子控件将要显示的位置
        // dx：水平方向滑动的偏移量（相对上一个move事件的变化量）
        //     dx = left - child.getLeft();
        // clamp:固定住
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
//            System.out.println("----left: " + left
//                    + "   当前的left: " + child.getLeft()
//                    +  "   dx: " + dx);

            // 限制主界面滑动的范围[0，mRange]
            if (child == mainView) {
                left = fixLeft(left);
            }

            return left;
        }

        // (3) 返回子控件水平方向拖动的最大范围
        // 注意：实际上不会真正限制子界面滑动的范围
        // 作用：如果要实现子控件的拖动，需要返回大于0的值
        // Range: 范围
        @Override
        public int getViewHorizontalDragRange(View child) {
            return mRange;
        }

        // (4) 滑动时位置发生改变就会调用此方法
        // 要处理的逻辑：关联界面滑动，自定义事件监听，伴随动画
        // changedView： 被拖动的子控件
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            // a.关联界面滑动
            // 当滑动菜单时，同时滑动主界面
            if (changedView == menuView) {
                // 让菜单保持位置不变，始终是显示在（0,0）
                menuView.layout(0, 0, mWidth, mHeight);

                // 主界面的新的左边界面(left)
                int newLeft = mainView.getLeft() + dx;
                newLeft = fixLeft(newLeft);

                // 当滑动菜单时，同时滑动主界面, 菜单滑动了dx,主界面也滑动dx
                mainView.layout(newLeft, 0, newLeft + mWidth, mHeight);
            }

            // b, 自定义事件监听(打开，关闭，滑动)
            listenDragStatus();

            // c, 伴随动画
            animateChildren();
        }

        // (5) 拖动结束松开手时调用
        // 要处理的逻辑： 关闭或打开侧滑菜单
        // releasedChild: 用户拖动的子控件
        // xvel: 水方向的速度, 往右甩动时值为正值
        // Released: 释放
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            System.out.println("----xvel: " + xvel);

            if (xvel > 0) { // 往右甩动时
                open();
            } else if (xvel == 0 && mainView.getLeft() >= mRange / 2) {
                // 中线的右边速度为0松开手
                open();
            } else {
                close();
            }
        }
    };

    /**
     * 估值器
     *
     * @return
     */
    private float evaluate(float start, float end, float percent) {
        // 估值器：根据开始值、结束值和百分比，计算变化的中间值
        // 要计算的值 = 开始值 + (结束值 - 开始值) * 百分比

        // 系统的估值器
        // FloatEvaluator
        // IntEvaluator
        // ArgbEvaluator

//        return new FloatEvaluator().evaluate(percent, start, end);
        return  start + (end - start) * percent;
    }

    /** 子界面实现伴随动画效果 */
    private void animateChildren() {
        // 主界面：   0     10（10%）   50（50%） ....  100
        // 菜单界面： -10   -9          -5              0

        // 百分比相等：
        // 主界面移动的百分比 == 菜单界移动的百分比
        // 主界面当前的left - 0 / 100 - 0 == (菜单界面的left - (-10)) / (0 - (-10))
        // 主界面移动的百分比 == (菜单界面的left - 开始值) / （结束值 - 开始值)
        // 主界面移动的百分比 * （结束值 - 开始值） == (菜单界面的left - 开始值)
        // 菜单界面的left = 开始值  + 百分比 * （结束值 - 开始值）

        // 估值器：根据开始值、结束值和百分比，计算变化的中间值
        // 要计算的值 = 开始值 + (结束值 - 开始值) * 百分比

        float percent = ((float)mainView.getLeft()) / mRange;
        // (1) 菜单界面：
        // 平移: [-mWidth/4,  0]
        menuView.setTranslationX(evaluate(-mWidth/ 4, 0, percent));
        // 缩放[0.5, 1]
//        menuView.setScaleX(evaluate(0.5f, 1f, percent));
//        menuView.setScaleY(evaluate(0.5f, 1f, percent));

        // 透明度[0.5, 1]
//        menuView.setAlpha(evaluate(0.5f, 1f, percent));

        // (2) 主界面： 缩放
        // 缩放[1, 0.8]
//        mainView.setScaleX(evaluate(1f, 0.8f, percent));
//        mainView.setScaleY(evaluate(1f, 0.8f, percent));

        // (3) 背景图片：亮度变化
        Drawable drawable = getBackground();
        if (drawable != null) {

//            int changeColor = (int) new ArgbEvaluator().evaluate(percent, Color.BLACK, Color.TRANSPARENT);
            int changeColor = (int) evaluateColor(percent, Color.BLACK, Color.TRANSPARENT);

            // 设置过滤颜色
            drawable.setColorFilter(changeColor, PorterDuff.Mode.SRC_ATOP);
        }
    }


    public Object evaluateColor(float fraction, Object startValue, Object endValue) {
        int startInt = (Integer) startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = (Integer) endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return (int)((startA + (int)(fraction * (endA - startA))) << 24) |
                (int)((startR + (int)(fraction * (endR - startR))) << 16) |
                (int)((startG + (int)(fraction * (endG - startG))) << 8) |
                (int)((startB + (int)(fraction * (endB - startB))));
    }

    /**
     * 自定义事件监听(打开，关闭，滑动)
     */
    private void listenDragStatus() {
        // 主界面的左边界(left)
        int left = mainView.getLeft();
        if (left == 0) { // 关闭
            mCurrentStatus = DragStatus.CLOSE;
        } else if (left == mRange) { // 打开
            mCurrentStatus = DragStatus.OPEN;
        } else { // 正在拖动
            mCurrentStatus = DragStatus.DRAGGING;
        }

        // 3. 当事件发生时，调用监听器对应的方法
        if (mOnDragListener != null) {
            if (mCurrentStatus == DragStatus.OPEN) {
                mOnDragListener.onOpen();
            } else if (mCurrentStatus == DragStatus.CLOSE) {
                mOnDragListener.onClose();
            } else { // 拖动
                float percent = 1f * mainView.getLeft() / mRange;
                mOnDragListener.onDragging(percent);
            }
        }

    }


    /** 关闭侧滑菜单 */
    private void close() {
        menuView.layout(0, 0, mWidth, mHeight);
        // mainView.layout(0, 0, mWidth, mHeight);

        // 平滑地移动第一步：
        // 让主界面平滑地移动到（0，0）的位置
        dragHelper.smoothSlideViewTo(mainView, 0, 0);
        // Scroller
        // 刷新界面显示：invalidate() -> onDraw()  -> computeScroll()
        ViewCompat.postInvalidateOnAnimation(this);
    }

    // 此方法会多次频繁地调用
    @Override
    public void computeScroll() {
        super.computeScroll();

        // 平滑地移动第二步：
        // 如果子控件没有移动到指向的位置，则继续刷新
        if (dragHelper.continueSettling(true)) {
            // 刷新界面显示：invalidate() -> onDraw()  -> computeScroll()
            ViewCompat.postInvalidateOnAnimation(this);
        }

    }

    /** 打开侧滑菜单 */
    private void open() {
        menuView.layout(0, 0, mWidth, mHeight);
        // mainView.layout(mRange, 0, mRange + mWidth, mHeight);

        // 平滑地移动第一步：
        // 让主界面平滑地移动到（mRange, 0）的位置
        dragHelper.smoothSlideViewTo(mainView, mRange, 0);
        // 刷新界面显示：invalidate() -> onDraw()  -> computeScroll()
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /** 限制主界面滑动的范围[0，mRange] */
    private int fixLeft(int left) {
        if (left < 0) {
            left = 0;
        } else if (left > mRange ) {
            left = mRange;
        }
        return left;
    }

    private void init() {
        // 1. 创建ViewDragHelper对象；
        dragHelper = ViewDragHelper.create(this, mCallback);

    }

    // 决定拦截事件的方法
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        // 如果侧滑菜单打开，则拦截事件，禁止主界面列表的滑动
        // ev.getX() > mRange: 说明滑动的是主界面
        if (isOpen() && ev.getX() > mRange) {
            return true;
        }

        // 2. 让ViewDragHelper决定是否拦截触摸事件；
        return dragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 3. 让ViewDragHelper处理触摸事件；
        dragHelper.processTouchEvent(event);

//        switch (event.getAction()) {
//            case  MotionEvent.ACTION_DOWN:
//                System.out.println("--------down");
//                break;
//            case  MotionEvent.ACTION_MOVE:
//                System.out.println("--------move");
//                break;
//        }

        // 按下时需要返回true，才能接收到后续的move和up事件
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return true;
        }

        return super.onTouchEvent(event);
    }

    // 布局Inflate完成后调用
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // 健壮性判断
        if (getChildCount() < 2) {
            throw new IllegalStateException("DragLayout至少要有两个子控件");
        }

        menuView = getChildAt(0);
        mainView = getChildAt(1);
    }

    // 测量控件宽高
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        // 最大范围为屏幕宽度的80%
        mRange = (int) (mWidth * 0.8f);
    }



    //============自定义事件监听(打开，关闭，滑动)=（begin）===============
    public enum  DragStatus {
        OPEN, CLOSE, DRAGGING
    }

    /** 当前侧滑菜单的拖动状态 */
    private DragStatus mCurrentStatus = DragStatus.CLOSE;

    /** 侧滑菜单是否打开 */
    public boolean isOpen() {
        return mCurrentStatus == DragStatus.OPEN;
    }

    // 1. 自定义接口
    public interface OnDragListener {

        public void onOpen();
        public void onClose();

        /** 主界面拖动位置的百分比 */
        public void onDragging(float percent);
    }

    // 2. 定义监听器并提供设置监听器的set方法
    private OnDragListener mOnDragListener;

    public void setOnDragListener(OnDragListener onDragListener) {
        this.mOnDragListener = onDragListener;
    }


    //============自定义事件监听(打开，关闭，滑动)=（end）===============
}











