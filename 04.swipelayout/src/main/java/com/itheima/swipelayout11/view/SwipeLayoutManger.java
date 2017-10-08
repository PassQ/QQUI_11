package com.itheima.swipelayout11.view;

/**
 * 用来管理列表中的SwipeLayout
 */
public class SwipeLayoutManger {

    // 饿汉单例
    private static SwipeLayoutManger mInstance = new SwipeLayoutManger();

    private SwipeLayoutManger(){
    }

    public static SwipeLayoutManger getInstance() {
        return mInstance;
    }

    /** 记录列表中已打开的SwipeLayout */
    private SwipeLayout mOpenSwipeLayout;

    /** 获取列表中已打开的SwipeLayout */
    public SwipeLayout getOpenSwipeLayout() {
        return mOpenSwipeLayout;
    }

    /** 保存列表中已打开的SwipeLayout */
    public void saveOpenSwipeLayout(SwipeLayout openSwipeLayout) {
        this.mOpenSwipeLayout = openSwipeLayout;
    }

    /** 清空列表中已打开的SwipeLayout */
    public void clearOpenSwipeLayout() {
        this.mOpenSwipeLayout = null;
    }

    /** 关闭列表中已打开的SwipeLayout */
    public void closeOpenSwipeLayout() {
        if (mOpenSwipeLayout != null) {
            mOpenSwipeLayout.close();
            mOpenSwipeLayout = null;
        }
    }

    /** 列表中是否已经打开了一个SwipeLayout */
    public boolean hasOpenSwipeLayout() {
        return mOpenSwipeLayout != null;
    }

}












