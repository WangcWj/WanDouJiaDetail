package com.wang.customlinear;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.wang.customlinear.util.DisplayUtils;

public class DetailActivity extends AppCompatActivity {
    CustomLinearLayout customLinearLayoutl;
    LinearLayout ll_root;
    private ColorDrawable mRootCDrawable;
    private int mColorInitAlpha =150;
    ScrollView scrollView;
    ImageView mIconImageView;
    LinearLayout mContentLl;
    LinearLayout ll_title,ll_bottom;
    TextView textview_appname;

    //接收过来的参数
    private int mViewMarginTop;
    private int mImageId;
    private String mAppName;
    private boolean initData;

    private int mContentTopOffsetNum;
    private int mContentBottomOffsetNum;
    private int mImageLeftOffsetNum;
    private int mImageTopOffsetNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        receiveParams();
        initView();
        initAnimationData();
    }

    private void initView() {
        //设置root布局view的背景透明度
        LinearLayout rootLl = (LinearLayout) findViewById(R.id.ll_root);
        Drawable rootBgDrawable = rootLl.getBackground();
        mRootCDrawable = (ColorDrawable) rootBgDrawable;
        mRootCDrawable.setAlpha(mColorInitAlpha);

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        customLinearLayoutl = (CustomLinearLayout) findViewById(R.id.ll_sv_root);
        mIconImageView = (ImageView) findViewById(R.id.imageview_icon);
        mIconImageView.setImageResource(mImageId);

        mContentLl = (LinearLayout) findViewById(R.id.ll_content);
        ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
        textview_appname = (TextView) findViewById(R.id.textview_appname);
        textview_appname.setText(mAppName);
        ll_title = (LinearLayout) findViewById(R.id.ll_title);

        mImageTopOffsetNum = getResources().getDimensionPixelOffset(R.dimen.title_view_height);

        //设置初始化的位置
        customLinearLayoutl.setContentInitMarginTop(mViewMarginTop);
        mContentTopOffsetNum = mViewMarginTop - getResources().getDimensionPixelOffset(R.dimen.view_height);

        /**
         *  activity 关闭回调
         */
        customLinearLayoutl.setOnCloseListener(new CustomLinearLayout.OnCloseListener() {
            @Override
            public void onClose() {
                finish();
                overridePendingTransition(0, 0);
            }
        });

        /**
         *  下拉拖动时候回调修改root背景色的透明度
         */
        customLinearLayoutl.setOnUpdateBgColorListener(new CustomLinearLayout.OnUpdateBgColorListener() {
            @Override
            public void onUpdate(float ratio) {
                mRootCDrawable.setAlpha((int) (mColorInitAlpha - mColorInitAlpha * ratio));
            }
        });
    }
    private void receiveParams() {
        Intent intent = getIntent();
        mViewMarginTop = intent.getIntExtra("viewMarginTop", 0);
        mImageId = intent.getIntExtra("imageId", 0);
        mAppName = intent.getStringExtra("appName");
    }

    private void initAnimationData() {
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(!initData) {
                    mContentBottomOffsetNum = scrollView.getMeasuredHeight() - mContentLl.getBottom();
                    customLinearLayoutl.setInitBottom(mContentLl.getBottom());
                    customLinearLayoutl.setAnimationStatus(true);
                    customLinearLayoutl.setLayoutImageView(true);
                    mImageLeftOffsetNum = (DisplayUtils.getScreenWidth(DetailActivity.this) - mIconImageView.getWidth()) / 2 - getResources().getDimensionPixelOffset(R.dimen.icon_margin);
                    initData = true;
                    startAnimation();
                }
            }
        });
    }
    private void startAnimation() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1).setDuration(400);
        valueAnimator.setStartDelay(100);
        valueAnimator.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float ratio = (float) animation.getAnimatedValue();
                //内容布局顶部偏移量
                int contentTopOffset = (int) (ratio * mContentTopOffsetNum);
                //内容布局底部偏移量
                int contentBottomOffset = (int) (ratio * mContentBottomOffsetNum);
                //图片左边偏移量
                int imageLeftOffset = (int) (ratio * mImageLeftOffsetNum);
                //图片上边偏移量
                int imageTopOffset =  (int) (ratio * mImageTopOffsetNum);
                customLinearLayoutl.setAllViewOffset(mViewMarginTop - contentTopOffset, contentBottomOffset, imageLeftOffset, imageTopOffset);
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                customLinearLayoutl.setAnimationStatus(false);
                ll_bottom.setVisibility(View.VISIBLE);
                ll_title.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            if(customLinearLayoutl != null) customLinearLayoutl.startAnimation(customLinearLayoutl.getCenterVisibleViewHeight(), false, 0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
