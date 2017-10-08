package com.itheima.swipelayout11.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 侧滑删除控件
 */
public class SwipeLayout extends FrameLayout {

    /** 内容布局 */
    private View contentView;

    /** 操作菜单布局 */
    private View menuView;

    /** SwipeLayout的高 */
    private int mHeight;

    /** SwipeLayout的宽 */
    private int mWidth;

    /** 菜单子界面的宽度 */
    private int mMenuWidth;
    private ViewDragHelper mDragHelper;

    public SwipeLayout(Context context) {
        super(context);
        init();
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // 4. 处理Callback回调方法
    ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {

        // （1） 是否捕获子控件，如果返回true，则可以拖动child
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        // (2) 设置子界面水平方向将要显示的位置
        // 处理的逻辑： 限制子界面的滑动范围
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {

            // 限制contentView的滑动范围[-mMenuWidth, 0]
            if (child == contentView) {
                if (left < -mMenuWidth) {
                    left = -mMenuWidth;
                } else if (left > 0) {
                    left = 0;
                }
            }

            // 限制menuView的滑动范围[mWidth-mMenuWidth, mWidth]
            if (child == menuView) {
                if (left < mWidth-mMenuWidth) {
                    left = mWidth-mMenuWidth;
                } else if (left > mWidth) {
                    left = mWidth;
                }
            }
            return left;
        }

//        @Override
//        public int getViewHorizontalDragRange(View child) {
//            return 1;
//        }

        // (3) 位置发生改变时调用
        // 要处理的逻辑：关联两个内容界面与菜单界面的滑动
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            // 当滑动内容界面时，同时滑动菜单界面
            if (changedView == contentView) {
                // contentView滑动了多少，则menuView对应地也滑动多少
                menuView.offsetLeftAndRight(dx);
            } else if (changedView == menuView) {
                // 当滑动菜单界面时，同时滑动内容界面
                // menuView滑动了多少，则contentView对应地也滑动多少
                contentView.offsetLeftAndRight(dx);
            }

            // if (左右滑动)
            if (Math.abs(dx) > Math.abs(dy)) {
                // 请求父控件(ListView)不要拦截事件, 把事件交给当前控件（SwipeLayout）处理
                // 这样：ListView就不会上下滑动了
                requestDisallowInterceptTouchEvent(true);
            } else {   // 上下滑
            }

            // 位置发生改变后，刷新界面，解决滑动显示的bug
            invalidate();
        }

        // (4) 松开手时，平滑地打开或关闭SwipeLayout
        // xvel: 松开手时，水平方向的速度，往右甩会为正值
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (xvel > 0) { // 往右甩动，需要关闭
                close();
            } else if (xvel == 0 && contentView.getLeft() > -mMenuWidth / 2) {
                // 中线右边速度为0松开手，也需要关闭
                close();
            } else {
                open();
            }
        }
    };

    /** 平滑关闭SwipeLayout */
    public void close() {
        // 平滑地移动某个子控件到某个位置
        // 关闭，移动到（0，0）
        mDragHelper.smoothSlideViewTo(contentView, 0, 0);
        // invalidate -> onDraw()  -> computeScroll()
        ViewCompat.postInvalidateOnAnimation(this);

        SwipeLayoutManger.getInstance().clearOpenSwipeLayout();
    }

    /** 平滑打开SwipeLayout */
    private void open() {
        // 打开，移动到（-mMenuWidth，0）
        mDragHelper.smoothSlideViewTo(contentView, -mMenuWidth, 0);
        // invalidate -> onDraw()  -> computeScroll()
        ViewCompat.postInvalidateOnAnimation(this);

        SwipeLayoutManger.getInstance().saveOpenSwipeLayout(this);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mDragHelper.continueSettling(true)) {
            // invalidate -> onDraw()  -> computeScroll()
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void init() {
        // 1. 创建ViewDragHelper对象
        mDragHelper = ViewDragHelper.create(this, mCallback);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 2. 让mDragHelper决定是否拦截事件
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // if (用户按下 && 列表已经打开了一个SwipeLayout
        //             && [优化一]尝试打开另外一个SwipeLayout时才需要关闭)
        if (event.getAction() == MotionEvent.ACTION_DOWN
                && SwipeLayoutManger.getInstance().hasOpenSwipeLayout()
                && SwipeLayoutManger.getInstance().getOpenSwipeLayout() != this) {

            // 则需要关闭上一次打开的SwipeLayout
            SwipeLayoutManger.getInstance().closeOpenSwipeLayout();

            // [优化三]如果已经打开了一个SwipeLayout
            // 请求父控件（也就是ListView）不要拦截事件
            // 这样：列表就无法上下滑动了
            requestDisallowInterceptTouchEvent(true);

            // [优化二]直接返回，不把DOWN事件交给ViewDragHelper的processTouchEvent处理，
            // 这样，当已打开了一个SwipeLayout时，则无法再滑动打开另一个SwipeLayout了
            return true;
        }

        // 3. 让ViewDragHelper处理触摸事件
        mDragHelper.processTouchEvent(event);

        // 按下时需要返回true，以便持续地接收后续的move和up事件
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return true;
        }

        return super.onTouchEvent(event);
    }

    // 填充结束后调用
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (getChildCount() < 2) {
            throw new IllegalStateException("SwipeLayout至少要有两个子界面");
        }

        menuView = getChildAt(0);
        contentView = getChildAt(1);
    }

    // 测量
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

//        // 如果继承ViewGroup,需要主动测量子界面的宽高
//        for (int i = 0; i < getChildCount(); i++) {
//            // 主动测量每一个子界面
//            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
//        }

        // 获取尺寸大小
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();

        mMenuWidth = menuView.getMeasuredWidth();
    }

//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//
//        // 获取尺寸大小
//        mHeight = getMeasuredHeight();
//        mWidth = getMeasuredWidth();
//        mMenuWidth = menuView.getMeasuredWidth();
//    }

    // 作用：设置子界面显示的位置
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // super.onLayout(changed, left, top, right, bottom);

        // 摆放子界面的位置
        contentView.layout(0, 0, mWidth, mHeight);
        menuView.layout(mWidth, 0, mWidth + mMenuWidth, mHeight);
    }
}


















