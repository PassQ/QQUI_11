package com.itheima.quickindex11.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class LetterBar extends View {

    /** 索引字母颜色 */
    private static final int LETTER_COLOR = 0xff595959;

    /** 字母索引条背景颜色 */
    private static final int BG_COLOR = 0xffB0B0B0;

    /** 26个字母 */
    public static final String[] LETTERS = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };

    private Paint mPaint;

    /** LetterBar的高 */
    private float mHeight;

    /** LetterBar的宽 */
    private float mWidth;

    /** 单元格的高度 */
    private float mCellHeight;

    private TextView tvFirstLetter;

    public LetterBar(Context context) {
        super(context);
        init();
    }

    public LetterBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(LETTER_COLOR);
        mPaint.setAntiAlias(true);  // 去锯齿，让字体的边缘变得平滑
        mPaint.setTextSize(dp2px(14));   //14sp
    }

    /** dp 转px */
    public int dp2px(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    /** 获取字符串的宽度 */
    public int getTextWidth(String text) {
        Rect rect = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), rect);
        return  rect.width();
    };

    /** 获取字符串的高度 */
    public int getTextHeight(String text) {
        Rect rect = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), rect);
        return  rect.height();
    };

    @Override
    protected void onDraw(Canvas canvas) {
        // 绘制26个字母
        for (int i = 0; i < LETTERS.length; i ++) {

//            if (i == index) { // 按下，要高亮显示
//                mPaint.setColor(Color.RED);
//            } else {    // 使用默认颜色
//                mPaint.setColor(Color.BLACK);
//            }

            String letter = LETTERS[i];

            // x = mWidth/2 - 字母宽度/2
            // y = mCellHeight/2 + 字母高度/2 + mCellHeight * k(k的值为0,1,2...)
            float x = mWidth / 2 - getTextWidth(letter) /2;
            float y = mCellHeight / 2 + getTextHeight(letter) / 2 + mCellHeight * i;
            // 绘制字母
            canvas.drawText(letter, x, y, mPaint);

            // 绘制分割线
            canvas.drawLine(0, mCellHeight * (i + 1), mWidth, mCellHeight * (i + 1), mPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();

        // mCellHeight = mHeight / 字母的个数;
        mCellHeight = ((float)mHeight) / LETTERS.length;
    }

    /** 选中的字母的索引 */
    int index = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                // 根据按下的位置获取选中的字母的索引（0，1，2）
                index = (int) (event.getY() / mCellHeight);
                if (index >= 0 && index < LETTERS.length) {
                    // 选中的字母
                    String letter = LETTERS[index];
                    // System.out.println("----选中的字母：" + letter);

                    tvFirstLetter.setVisibility(View.VISIBLE);
                    tvFirstLetter.setText(letter);

                    // 3. 当事件发生时，调用监听器对应的方法
                    if (mOnLetterSelectedListener != null) {
                        mOnLetterSelectedListener.onLetterSelected(index, letter);
                    }
                }

                // 会调用onDraw重绘界面
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:

                // 根据按下的位置获取选中的字母的索引（0，1，2）
                int tempindex = (int) (event.getY() / mCellHeight);
                if (tempindex >= 0 && tempindex < LETTERS.length) {

                    // 与上一次选中的字母不一样时，才打印
                    if (tempindex != index) {
                        // 选中的字母
                        String letter = LETTERS[tempindex];
                        // System.out.println("----选中的字母：" + letter);

                        tvFirstLetter.setVisibility(View.VISIBLE);
                        tvFirstLetter.setText(letter);

                        // 3. 当事件发生时，调用监听器对应的方法
                        if (mOnLetterSelectedListener != null) {
                            mOnLetterSelectedListener.onLetterSelected(tempindex, letter);
                        }
                    }

                    index = tempindex;
                }

                // 会调用onDraw重绘界面
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                tvFirstLetter.setVisibility(View.GONE);
                break;
        }

        // 按下时需要返回true，以便持续的接收后续的move和up事件
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return true;
        }

        return super.onTouchEvent(event);
    }

    public void setFirstLetterTextView(TextView tvFirstLetter) {
        this.tvFirstLetter = tvFirstLetter;
    }


    // 1. 自定义接口
    public interface OnLetterSelectedListener {

        /**
         *  选中字母改变了
         *
         * @param index 选中的字母的索引
         * @param letter 选中的字母
         */
        public void onLetterSelected(int index, String letter);
    }

    // 2. 定义监听器并提供设置监听器的set方法
    public OnLetterSelectedListener mOnLetterSelectedListener;

    public void setOnLetterSelectedListener(OnLetterSelectedListener listener) {
        this.mOnLetterSelectedListener = listener;
    }

}



















