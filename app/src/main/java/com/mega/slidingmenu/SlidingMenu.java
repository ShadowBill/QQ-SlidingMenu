package com.mega.slidingmenu;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * Created by shadow. on 2016/9/13 0013.
 */
public class SlidingMenu extends HorizontalScrollView {
    private static final String TAG = "TAG";
    private LinearLayout mWapper;
    private ViewGroup mMenu;
    private ViewGroup mContent;
    private int mScreenWidth;
    private int mMenuRightPadding = 50;
    private boolean once = false;
    private int mMenuWidth;

    private boolean isOpen;

    public SlidingMenu(Context context) {
        this(context, null);
    }

    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SlidingMenu, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.SlidingMenu_rightPadding:
                    mMenuRightPadding = a.getDimensionPixelSize(R.styleable.SlidingMenu_rightPadding,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()));
                    break;
                default:
                    break;
            }
        }
        Log.e(TAG, "mMenuRightPadding: " + mMenuRightPadding);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!once) {
            mWapper = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) mWapper.getChildAt(0);
            mContent = (ViewGroup) mWapper.getChildAt(1);
            mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth - mMenuRightPadding;
            Log.e(TAG, "onMeasure: " + mMenuWidth);
            mContent.getLayoutParams().width = mScreenWidth;
            once = true;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            this.scrollTo(mMenuWidth, 0);
            isOpen = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int actoin = ev.getAction();
        switch (actoin) {
            case MotionEvent.ACTION_UP:
                if (getScrollX() >= mMenuWidth / 2) {
                    this.smoothScrollTo(mMenuWidth, 0);
                    isOpen = false;
                } else {
                    this.smoothScrollTo(0, 0);
                    isOpen   = true;
                }
                return true;
        }
        return super.onTouchEvent(ev);
    }

    private void openToggle(){
        if (isOpen) return;
        smoothScrollTo(0, 0);
        isOpen = true;
    }

    private void closeToggle() {
        if (!isOpen) return;
        smoothScrollTo(mMenuWidth,0);
        isOpen = false;
    }

    public void toggle() {
        if (!isOpen) {
            openToggle();
        } else {
            closeToggle();
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        float scale = l * 1.0f / mMenuWidth;
//        ViewHelper.setTranslationX(mMenu, mMenuWidth * scale);

        mContent.setPivotX(0);
        mContent.setPivotY(mContent.getHeight()/2);
        /**
         * scale : 1 -> 0
         * 内容区域 ： leftScale ０.7 + 0.3 * scale
         */
        ObjectAnimator.ofFloat(mContent, "scaleX", 0.7f + 0.3f * scale).setDuration(0).start();
        ObjectAnimator.ofFloat(mContent, "scaleY", 0.7f + 0.3f * scale).setDuration(0).start();
        /**
         * 菜单区域：缩放 0.7 ->1      rightScale      1f - 0.3f * scale
         *          透明度 ：0.6 -> 1  rightAlpha     0.6+0.4*(1-scale)
         */
        ObjectAnimator.ofFloat(mMenu,"translationX",mMenuWidth * scale * 0.7f).setDuration(0).start();
        ObjectAnimator.ofFloat(mMenu, "scaleX", 1f - 0.3f * scale).setDuration(0).start();
        ObjectAnimator.ofFloat(mMenu, "scaleY", 1f - 0.3f * scale).setDuration(0).start();
        ObjectAnimator.ofFloat(mMenu, "alpha", 0.4f + 0.6f * (1 - scale)).start();


    }
}
