package com.itheima.gooview11.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.itheima.gooview11.util.GeometryUtil;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by Administrator on 2016/12/30.
 */
public class GooView extends View {

    private PointF mStickCenter = new PointF(150, 150);
    private float mStickRadius = 20;
    private PointF[] mStickPoints = new PointF[] {
            new PointF(300, 300),       // A点
            new PointF(300, 400)        // D点
    };

    private PointF mDragCenter = new PointF(100, 100);
    private float mDragRadius = 20;
    private PointF[] mDragPoints = new PointF[] {
            new PointF(100, 300),       // B点
            new PointF(100, 400)        // C点
    };

    /** 控制点的坐标 */
    private PointF mCtrlPoint = new PointF(200, 350);

    private Paint mPaint;

    /** 断开的最大范围 */
    private float mRange = 80;

    public GooView(Context context) {
        super(context);
        init();
    }

    public GooView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);  // 去锯齿
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // 计算固定圆的半径，圆心连线的距离越大，则固定圆的半径越小
        float  tempStickRadius = calStickRadius();


        // 使用工具类计算变化的点（ABCD交叉点，控制点）
        float dx = mDragCenter.x - mStickCenter.x;
        float dy = mDragCenter.y - mStickCenter.y;

        // 圆心连线斜率的计算：
        // 斜率k = tanα = （y2-y1）/（x2-x1）
        double lineK = 0;
        if (dx != 0) {
            lineK = dy / dx;
        }
        // 计算AD的坐标点
        mStickPoints = GeometryUtil.getIntersectionPoints(mStickCenter, tempStickRadius, lineK);
        // 计算BC的坐标点
        mDragPoints = GeometryUtil.getIntersectionPoints(mDragCenter, mDragRadius, lineK);
        // 圆心连线的中点
        mCtrlPoint = GeometryUtil.getMiddlePoint(mStickCenter, mDragCenter);

        // 没有消失才绘制三部分
        if (!mDisappear) {
            // 没有断开才绘制固定圆和连接部分
            if (!mOutOfRange) {
                // (1) 绘制固定圆
                canvas.drawCircle(mStickCenter.x,mStickCenter.y, tempStickRadius, mPaint);

                // (3) 绘制连接部分
                Path path = new Path();
                path.moveTo(mStickPoints[0].x, mStickPoints[0].y);                  // A点
                // quadTo: 二次方法的贝塞尔曲线
                path.quadTo(mCtrlPoint.x, mCtrlPoint.y, mDragPoints[0].x, mDragPoints[0].y);        // AB： 曲线
                path.lineTo(mDragPoints[1].x, mDragPoints[1].y);                  // BC： 直线
                path.quadTo(mCtrlPoint.x, mCtrlPoint.y, mStickPoints[1].x, mStickPoints[1].y);        // CD:  曲线
                path.close();

                canvas.drawPath(path, mPaint);


                // 绘制交叉点（仅供参考）
                mPaint.setColor(Color.BLUE);
                canvas.drawCircle(mStickPoints[0].x, mStickPoints[0].y, 5, mPaint); // A
                canvas.drawCircle(mStickPoints[1].x, mStickPoints[1].y, 5, mPaint); // D
                canvas.drawCircle(mDragPoints[0].x, mDragPoints[0].y, 5, mPaint);   // B
                canvas.drawCircle(mDragPoints[1].x, mDragPoints[1].y, 5, mPaint);   // C
                mPaint.setColor(Color.RED);
            }

            // (2) 绘制拖动圆
            canvas.drawCircle(mDragCenter.x, mDragCenter.y, mDragRadius, mPaint);
        }

        // 绘制断开的最大范围（仅供参考）
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mStickCenter.x, mStickCenter.y, mRange, mPaint);   // C
        mPaint.setStyle(Paint.Style.FILL);
    }

    /** 计算固定圆的半径，圆心连线的距离越大，则固定圆的半径越小 */
    private float calStickRadius() {
        float start = mStickRadius;
        float end = mStickRadius * 0.4f;

        float dis = GeometryUtil.getDistanceBetween2Points(mDragCenter, mStickCenter);
//        if (dis > mRange) {
//            dis = mRange;
//        }

        dis = Math.min(dis, mRange);
        float percent = dis / mRange;
        float value = start + (end - start) * percent;
        return value;
    }

    /** 是否已断开 */
    private boolean mOutOfRange = false;

    /** 是否消失 */
    private boolean mDisappear = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mOutOfRange = false;
                mDisappear = false;
            case MotionEvent.ACTION_MOVE:

                // 把手指按下的点设置为拖动圆的圆心
                mDragCenter.set(event.getX(), event.getY());
                invalidate();   // 刷新界面，会调用onDraw方法重新绘制界面

                // a.拖动超出最大范围，则断开； （Move事件）
                float dis = GeometryUtil.getDistanceBetween2Points(mDragCenter, mStickCenter);
                if (dis > mRange) { // 断开了
                    mOutOfRange = true;
                    invalidate();       // 刷新界面，不再绘制固定圆和连接部分
                }

                break;
            case MotionEvent.ACTION_UP:

                float dis2 = GeometryUtil.getDistanceBetween2Points(mDragCenter, mStickCenter);
                if (dis2 > mRange) { // 超出最大范围松开手
                    // b.超出了最大范围松开手， 则消失；  (Up事件）
                    mDisappear = true;
                    invalidate();   // 刷新界面，不再绘制三部分

                } else {// 没有超出最大范围松开手

                    if (mOutOfRange) {
                        // c.没超出了最大范围松开手： 有断开过, 则恢复显示； (Up事件）
                        mDragCenter.set(mStickCenter.x, mStickCenter.y);
                        invalidate();
                    } else {
                        // d.没超出了最大范围松开手： 没有断开过，则弹回去； (Up事件）

                        final PointF start = new PointF(mDragCenter.x, mDragCenter.y);
                        final PointF end = mStickCenter;

                        ValueAnimator animator = ValueAnimator.ofInt(1);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {

                                float percent = animation.getAnimatedFraction();
                                PointF point = GeometryUtil.getPointByPercent(start, end, percent);

                                // 更新拖动圆的圆心
                                mDragCenter.set(point.x, point.y);
                                invalidate();
                            }
                        });

                        animator.setInterpolator(new OvershootInterpolator(3));
                        animator.setDuration(300);
                        animator.start();

                    }
                }

                break;
        }
        return true;
    }
}














