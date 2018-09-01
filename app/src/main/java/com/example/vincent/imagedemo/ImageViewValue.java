package com.example.vincent.imagedemo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * @author Vincent Vincent
 * @version v1.0
 * @name ImageDemo
 * @page com.example.vincent.imagedemo
 * @class describe
 * @date 2018/8/30 14:53
 */
public class ImageViewValue extends View {
    private static final String TAG = "数据";
    private Context mContext;
    private float mViewWidth;
    private float mViewHeight;
    //当前值
    private float currentValue = 0;
    //最小值
    private float minValue = 0f;
    //最大值
    private float maxValue = 40f;
    private Paint mPaint;
    private float bgHeight = 40f;
    private float tagHeight = 20;
    private float tagWidth = 10;
    private float tagTopMargin;
    //这是根据当前值计算的距离左边的距离
    private float tartgetMargin;


    public ImageViewValue(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mViewWidth = w;
        mViewHeight = h;
        tagTopMargin = (mViewHeight - bgHeight - tagHeight)/2;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void init(Context context) {
        this.mContext = context;
        bgHeight = DpUtil.dp2px(mContext,bgHeight);
        tagHeight = DpUtil.dp2px(mContext,tagHeight);
        tagWidth = DpUtil.dp2px(mContext,tagWidth);
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBgImg(canvas);
        drawTag(canvas);
    }

    private void drawTag(Canvas canvas) {
        Bitmap tagBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.icon_water_mark);
        Bitmap newTagBitmap =  createScaleBitmap(tagBitmap,(int) tagWidth,(int)tagHeight);
        if(animatedValue > mViewWidth){
            animatedValue = mViewWidth;
        }
        canvas.drawBitmap(newTagBitmap, animatedValue,tagTopMargin,mPaint);
    }

    /**
     * 绘制背景标准图
     * @param canvas
     */
    private void drawBgImg(Canvas canvas) {
        Bitmap bgBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.icon_moisture_range);
//        Log.e("图片平滑", "drawBgImg: "+bgBitmap.getHeight() );
        Bitmap newBgBitmap = createScaleBitmap(bgBitmap,(int) mViewWidth,(int)bgHeight);
        canvas.drawBitmap(newBgBitmap,0,tagTopMargin + tagHeight,mPaint);
        canvas.save();
    }

    private Bitmap createScaleBitmap(Bitmap src, int dstWidth, int dstHeight) {
        // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响，我们这里是缩小图片，所以直接设置为false
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        if (src != dst) { // 如果没有缩放，那么不回收
            src.recycle(); // 释放Bitmap的native像素数组
        }
        return dst;
    }

    private float startValue = 0;
    private float animatedValue = 0;

    public void setCurrentValue(float currentValue) {
        this.currentValue = (currentValue - 20);
        if(currentValue <=  minValue){
            tartgetMargin = 0;
        }else if(currentValue > maxValue){
            tartgetMargin = mViewWidth +  tagWidth/2;
        }else {
            //计算
            tartgetMargin = mViewWidth * 1.0f * currentValue/maxValue - tagWidth/2;
        }
        if(tartgetMargin > mViewWidth){
            tartgetMargin = mViewWidth - tagWidth;
        }
        Log.e("当前值", "drawTag: value -- >"+currentValue+" 动画值->"+animatedValue
                +" 目标值->"+tartgetMargin + " view宽度->" + mViewWidth+" min->"+minValue+" max->"+maxValue);
        ValueAnimator anim = ValueAnimator.ofFloat(startValue,tartgetMargin );
        anim.setDuration(1000);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                animatedValue = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        anim.start();

    }

    public void clearValue(){
        animatedValue = 0;
        invalidate();
    }

}
