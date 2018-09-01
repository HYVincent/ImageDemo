package com.example.vincent.imagedemo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;


/**
 * @author Vincent Vincent
 * @version v1.0
 * @name TestProject
 * @page com.example.administrator.testview
 * @class describe
 * @date 2018/8/10 15:52
 */
@SuppressLint("AppCompatCustomView")
public class WhiteDegreeImageView extends ImageView {

    private float width;
    private float height;
    private Paint mPaint;

    //目标值
    private float targetAngle;
    private float currentAngle = 0;

    private Canvas mCanvas;
    private Bitmap mBitmap;

    private float animatedValue;
    private Context mContext;
    private ValueAnimator.AnimatorUpdateListener animatorUpdateListener;
    private AnimStatusListener animFinishListener;

    private Bitmap bitmap;
    private Bitmap newBitmap;
    private Matrix matrix;
    //原图宽高比例
    private float scale;

    public WhiteDegreeImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPaint = new Paint();
        matrix = new Matrix();
        animatorUpdateListener = valueAnimator -> {
            animatedValue = (float) valueAnimator.getAnimatedValue();
            invalidate();
        };
    }
    private static final String TAG = "画图";
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h - DpUtil.dp2px(mContext,4);
        mBitmap = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_skin_type_bg);
        Log.d(TAG, "onSizeChanged: bitmap w= "+bitmap.getWidth()+"  bitmap h="+bitmap.getHeight()+" view w="+width+" view h="+height);
        //原图宽高比例
        scale = bitmap.getWidth()*1.0f / bitmap.getHeight() * 1.0f;
        newBitmap = zoomImg(bitmap, height*2.0f * scale, height*2.0f);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas rootCanvas) {
        matrix.reset();
        matrix.preTranslate(-1.0f * (height*2.0f * scale-width)/2.0f, - height);
        matrix.postRotate(animatedValue, width / 2, 0);
        mCanvas.drawBitmap(newBitmap, matrix, mPaint);
        rootCanvas.drawBitmap(newBitmap, matrix, mPaint);
    }


    public void setAnimStatusListener(AnimStatusListener animFinishListener) {
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
        ValueAnimator anim;
        anim = ValueAnimator.ofFloat(currentAngle, targetAngle);
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

}
