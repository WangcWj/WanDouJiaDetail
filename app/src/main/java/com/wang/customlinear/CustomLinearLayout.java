package com.wang.customlinear;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import com.wang.customlinear.util.BigDecimalUtils;

/**
 * Created by Wang on 2017/7/5.
 */

public class CustomLinearLayout extends LinearLayout {

    /*详情页的布局容器*/
    private LinearLayout mContentLL;
    /*详情页的软件图标*/
    private ImageView mIconImageView;
    /*软件图标起始的左上边距*/
    private int mMargin;
    /*mIsAnimation是否改变详情页布局容器的位置   mIsLayoutImageView是否改变权健图标的位置   */
    private boolean mIsAnimation, mIsLayoutImageView;
    private int mInitBottom;
    public int mContentMarginTop, mContentBottomOffset, mImageLeftOffset, mImageTopOffset, mTouchMoveOffset;


    private ScrollView mParentScrollView;
    private int mTitleViewHeight;
    /*软件详情可见布局的真实高度*/
    private int mCenterVisibleViewHeight;
    private int mContentLlHeight, mContentLlWidth, mIconImageViewHeight, mIconImageViewWidth;

    /**
     *  拖拽
     */
    private float mInitY;
    private int mTouchSlop;
    private boolean mIsDrag;
    private OnCloseListener mOnCloseListener;
    private OnUpdateBgColorListener mOnUpdateBgColorListener;


    public void setOnCloseListener(OnCloseListener onCloseListener) {
        mOnCloseListener = onCloseListener;
    }

    public void setOnUpdateBgColorListener(OnUpdateBgColorListener onUpdateBgColorListener) {
        mOnUpdateBgColorListener = onUpdateBgColorListener;
    }

    public CustomLinearLayout(Context context) {
        super(context);
    }

    public CustomLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMargin = context.getResources().getDimensionPixelOffset(R.dimen.icon_margin);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public CustomLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public int getCenterVisibleViewHeight() {
        return  mCenterVisibleViewHeight;
    }

    public void setInitBottom(int bottom) {
        mInitBottom = bottom;
    }

    public void setAnimationStatus(boolean isAnimation) {
        mIsAnimation = isAnimation;
    }

    public void setLayoutImageView(boolean layoutImageView) {
        mIsLayoutImageView = layoutImageView;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContentLL = (LinearLayout) findViewById(R.id.ll_content);
        mIconImageView = (ImageView) findViewById(R.id.imageview_icon);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int contentTop = mContentMarginTop + mTouchMoveOffset;
        /*这个是用来控制下拉的*/
        int bottom1 = contentTop + mContentLlHeight;
        /*这个是布局刚开始绘制时候的大小*/
        int bottom2 = mInitBottom + mContentBottomOffset;
        //开始变化的时候
        mContentLL.layout(0, contentTop, mContentLlWidth, !mIsAnimation ? bottom1 : bottom2);

        if(!mIsLayoutImageView) return;
        int left = mMargin + mImageLeftOffset;
        int top = mMargin + mImageTopOffset;
        mIconImageView.layout(left, top, left + mIconImageViewWidth, top + mIconImageViewHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int childNumHeight = 0;
        for(int i = 0; i < getChildCount(); i++) {
            childNumHeight += getChildAt(i).getMeasuredHeight();
        }
        if(childNumHeight > getMeasuredHeight()) {
            setMeasuredDimension(getMeasuredWidth(), childNumHeight);
        }

        mTitleViewHeight = getChildAt(0).getMeasuredHeight();
        mContentLlHeight = mContentLL.getMeasuredHeight();
        mContentLlWidth = mContentLL.getMeasuredWidth();
        mIconImageViewHeight = mIconImageView.getMeasuredHeight();
        mIconImageViewWidth = mIconImageView.getMeasuredWidth();

        if(mParentScrollView == null) mParentScrollView = (ScrollView) getParent();
        //这里是要剪掉顶部半透明的布局
        mCenterVisibleViewHeight = mParentScrollView.getHeight() - mTitleViewHeight;
    }

    public void setContentInitMarginTop(int marginTop) {
        mContentMarginTop = marginTop;
        requestLayout();
    }

    public void setAllViewOffset(int contentMarginTop, int contentBottomOffset, int imageLeftOffset, int imageTopOffset) {
        mContentMarginTop = contentMarginTop;
        mContentBottomOffset = contentBottomOffset;
        mImageLeftOffset = imageLeftOffset;
        mImageTopOffset = imageTopOffset;
        requestLayout();
    }

    public void setTouchMoveOffset(float touchMoveOffset) {
        if(touchMoveOffset < 0) touchMoveOffset = 0;
        mTouchMoveOffset = (int) touchMoveOffset;
        requestLayout();
        updateBgColor(mTouchMoveOffset);
    }

    public void updateBgColor(int offset) {
        if(mOnUpdateBgColorListener != null) {
            float ratio = BigDecimalUtils.divide(offset, mCenterVisibleViewHeight);
            if(ratio > 1) ratio = 1;
            if(ratio < 0) ratio = 0;
            mOnUpdateBgColorListener.onUpdate(ratio);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean consumption = true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInitY = event.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = event.getY();
                float yOffset = moveY - mInitY;
                int scrollY = mParentScrollView.getScrollY();
                Log.e("CHAO", "onTouchEvent: "+scrollY);
                //拖动
                if((mParentScrollView.getScrollY() <= 0 && moveY >= mInitY) || mIsDrag) {
                    setTouchMoveOffset(yOffset);
                    mIsDrag = true;
                    consumption = true;
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                    consumption = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                mIsDrag = false;
                boolean isUp = false;
                int animationMoveOffset;
                if(mContentLL.getTop() <= mCenterVisibleViewHeight / 2 + mTitleViewHeight) {
                    animationMoveOffset = mTouchMoveOffset;
                    isUp = true;
                } else {
                    animationMoveOffset = mParentScrollView.getHeight() - mContentLL.getTop();
                }
                startAnimation(animationMoveOffset, isUp, mTouchMoveOffset);
                break;
        }
        return consumption;
    }

    public void startAnimation(final int moveOffset, final boolean isUp, final int currentMoveOffset) {
        int duration = moveOffset / mTouchSlop * 10;
        if(duration <= 0) duration = 300;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1).setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float ratio = (float) animation.getAnimatedValue();
                if(isUp)
                    mTouchMoveOffset = (int) (moveOffset * (1 - ratio));
                else
                    mTouchMoveOffset = currentMoveOffset + (int) (moveOffset * ratio);
                requestLayout();
                updateBgColor(mTouchMoveOffset);
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(!isUp && mOnCloseListener != null)
                    mOnCloseListener.onClose();
            }
        });
        valueAnimator.start();
    }

    public interface OnCloseListener {
        void onClose();
    }

    public interface OnUpdateBgColorListener {
        void onUpdate(float ratio);
    }
}
