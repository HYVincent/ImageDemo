package com.example.vincent.imagedemo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.AccelerateDecelerateInterpolator;


/**
 * @author Vincent Vincent
 * @version v1.0
 * @name ImageDemo
 * @page com.example.vincent.imagedemo
 * @class describe
 * @date 2018/9/1 9:16
 */
public class MyView extends SurfaceView implements SurfaceHolder.Callback{

    private static final String TAG = "MyView";
    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        holder = this.getHolder();
        holder.addCallback(this);
        mPaint = new Paint();
        mPaint.setColor(ContextCompat.getColor(mContext, R.color.color_red_fa73a2));
        mPaint.setAntiAlias(true);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));

        matrix = new Matrix();

        mLineMargin = DpUtil.dp2px(mContext,mLineMargin);
        mCenterCircleRadius = DpUtil.dp2px(mContext,mCenterCircleRadius);
        mCircleRadius = mViewHeght;
        //可继续初始化自定义属性
       /* TypedArray typedArray = context.getTheme().obtainStyledAttributes(atr,
                R.styleable.MyLineChartView, 0, 0);*/
        animatorUpdateListener = valueAnimator -> {
            animatedValue = (float) valueAnimator.getAnimatedValue();
            Log.d(TAG, "init: -------------------------------"+animatedValue);
            invalidate();
        };
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mViewWidth = w;
        mViewHeght = h;
        mBitmap = Bitmap.createBitmap((int) mViewWidth, (int) mViewHeght, Bitmap.Config.ARGB_8888);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_skin_type_bg);
        Log.d(TAG, "onSizeChanged: bitmap w= "+bitmap.getWidth()+"  bitmap h="+bitmap.getHeight()+" view w="+mViewWidth+" view h="+mViewHeght);
        //原图宽高比例
        scale = bitmap.getWidth()*1.0f / bitmap.getHeight() * 1.0f;
        newBitmap = zoomImg(bitmap, mViewHeght*2.0f * scale, mViewHeght*2.0f);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        isRunning = false;
    }

    private SurfaceHolder holder;
    private boolean isRunning = true;
    private Context mContext;
    private Paint mPaint;
    private float mViewWidth;
    private float mViewHeght;
    private Canvas canvas;
    private ValueAnimator.AnimatorUpdateListener animatorUpdateListener;
    private WhiteDegreeImageView.AnimStatusListener animFinishListener;

    private Bitmap bitmap;
    private Bitmap newBitmap;
    private Matrix matrix;
    private Bitmap mBitmap;
    private float animatedValue;
    private float currentAngle;

    //目标值
    private float targetAngle;
    //原图宽高比例
    private float scale;

    private float mCircleRadius;
    private float mCenterCircleRadius = 15;
    private float mLineMargin = 10;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRunning = true;
        Thread thread = new Thread(() -> {
            while (isRunning){
                canvas = holder.lockCanvas();
                if(canvas != null){
                    matrix.reset();
                    matrix.preTranslate(-1.0f * (mViewHeght*2.0f * scale-mViewWidth)/2.0f, - mViewHeght);
                    matrix.postRotate(animatedValue, mViewWidth / 2, 0);
                    canvas.drawBitmap(newBitmap, matrix, mPaint);
                    drawLineAndCircle(canvas);
//                    Log.d(TAG, "surfaceCreated: ........................."+mViewWidth + " "+mViewHeght + " animatedValue="+animatedValue);
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        });
        thread.start();
    }

    /**
     * 绘制红色竖线和屏幕顶部中间的半圆
     * @param canvas
     */
    private void drawLineAndCircle(Canvas canvas) {
        mCircleRadius = mViewHeght;
        canvas.drawCircle(mViewWidth/2.0f,0,mCenterCircleRadius,mPaint);
        RectF rectF = new RectF();
        rectF.set(mViewWidth/2-DpUtil.dp2px(mContext,2),mCenterCircleRadius-1, mViewWidth/2+DpUtil.dp2px(mContext,2),
                mCircleRadius - DpUtil.dp2px(mContext,50));
        canvas.drawRect(rectF,mPaint);
    }

    // 等比缩放图片
    public Bitmap zoomImg(Bitmap bm, float newWidth, float newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = newWidth / width;
        float scaleHeight = newHeight / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
//        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    }

    public interface AnimStatusListener {

        void onStart();

        void onFinish();
    }

    public void defaultAngle() {
        targetAngle = 0;
        currentAngle = 0;
        animatedValue = 0;
        invalidate();
    }


    public void setAnimStatusListener(WhiteDegreeImageView.AnimStatusListener animFinishListener) {
        this.animFinishListener = animFinishListener;
    }


    public void setTargetAngle(float newAngle) {
        //保存之前的角度
        currentAngle = targetAngle;
        this.targetAngle = newAngle;
        if (currentAngle == targetAngle) {
            //目标值相同，直接结束动画
            if (animFinishListener != null) {
                animFinishListener.onFinish();
            }
            return;
        }
        //动画插值器
        ValueAnimator anim = ValueAnimator.ofFloat(currentAngle, targetAngle);
        anim.setDuration(1000);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(animatorUpdateListener);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (animFinishListener != null) {
                    animFinishListener.onStart();
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (animFinishListener != null) {
                    animFinishListener.onFinish();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        anim.start();
//        invalidate();
        postInvalidate();
    }

}
