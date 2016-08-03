package com.hss01248.roundprogressbar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

/**
 * Created by Administrator on 2016/6/7 0007.
 */
public class SmoothRoundProgressBar extends View {





//可以设置的值
    private int strokeWidth;
    private int startColor;
    private int endColor;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    private int duration;


    private RotateAnimation animation;


    private Paint ringPaint;
    private int[] colors;// 用于渐变
    private final RectF rectF = new RectF();
    private Paint halfRoundPaint;


    public SmoothRoundProgressBar(Context context) {
        super(context);
        init(context, null);
    }

    public SmoothRoundProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SmoothRoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SmoothRoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private int dip2px(int dp){
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }


    @SuppressWarnings("ResourceType")
    private void init(final Context context, final AttributeSet attrs) {




            strokeWidth = dip2px(7);

            startColor = Color.WHITE;
            endColor = Color.LTGRAY;
            duration = 1200;



        //获取系统定义的属性.注意此时拿到的layoutparams为空
         int[] attrsArray = new int[] {
        android.R.attr.id, // 0
        android.R.attr.background, // 1
        android.R.attr.layout_width, // 2
        android.R.attr.layout_height // 3
        };

         TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);
       // int id = ta.getResourceId(0, View.NO_ID);
       // Drawable background = ta.getDrawable(1);
        int layout_width = ta.getDimensionPixelSize(2, ViewGroup.LayoutParams.MATCH_PARENT);
       // int layout_height = ta.getDimensionPixelSize(3, ViewGroup.LayoutParams.MATCH_PARENT);
        ta.recycle();

        Log.e("height","height:"+layout_width);
        if (layout_width > 0){
            strokeWidth = (int) (layout_width  * 0.1 );
        }else {
            strokeWidth = dip2px(5);
        }





        //获取xml中设置的自定义属性
            TypedArray typedArray = null;
            try {
                typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyCircleProgressBar);
                strokeWidth = (int) typedArray.getDimension(R.styleable.MyCircleProgressBar_cpb_strokeWidth, strokeWidth);
                startColor = typedArray.getColor(R.styleable.MyCircleProgressBar_cpb_startColor, startColor);
                endColor = typedArray.getColor(R.styleable.MyCircleProgressBar_cpb_endColor, endColor);
                duration = typedArray.getInteger(R.styleable.MyCircleProgressBar_cpb_duration,duration);
            } finally {
                if (typedArray != null) {
                    typedArray.recycle();
                }
            }





        colors = new int[]{startColor,endColor};

        //圆环的画笔
        ringPaint = new Paint();
        ringPaint.setAntiAlias(true);
        ringPaint.setStrokeWidth(strokeWidth);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeJoin(Paint.Join.ROUND);
        ringPaint.setStrokeCap(Paint.Cap.ROUND);
        ringPaint.setColor(startColor);


        //开始的点的半圆画笔
        halfRoundPaint = new Paint();
        halfRoundPaint.setAntiAlias(true);
        halfRoundPaint.setColor(endColor);
        halfRoundPaint.setStyle(Paint.Style.FILL);


    }




    /**
     * @param color ColorInt
     */
    public void setEndColor(final int color) {
        if (this.endColor != color) {
            this.endColor = color;
            colors = new int[]{startColor,endColor};
            invalidate();
        }
    }

    public int getEndColor() {
        return this.endColor;
    }

    /**
     * @param width px
     */
    public void setStrokeWidth(final int width) {
        if (this.strokeWidth != width) {
            this.strokeWidth = width;
            // 画描边的描边变化
            ringPaint.setStrokeWidth(width);

            // 会影响measure
            requestLayout();
        }
    }

    public int getStrokeWidth() {
        return this.strokeWidth;
    }





    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //拿到要画的右边的半圆的矩形
        this.rectF.left = getMeasuredWidth()  - strokeWidth;
        this.rectF.top = getMeasuredHeight()/2 - strokeWidth/2;
        this.rectF.right = getMeasuredWidth()   ;
        this.rectF.bottom = getMeasuredHeight()/2 + strokeWidth/2;

    }




    // 目前由于SweepGradient赋值只在构造函数，无法pre allocate & reuse instead
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int restore = canvas.save();

        final int cx = getMeasuredWidth() / 2;
        final int cy = getMeasuredHeight() / 2;
        final int radius = getMeasuredWidth() / 2 - strokeWidth / 2;



        //渐变
        final SweepGradient sweepGradient = new SweepGradient(cx, cy, colors, null);
        ringPaint.setShader(sweepGradient);


        canvas.drawCircle(cx, cy, radius, ringPaint);
       // canvas.restore();

        //画半弧和半圆
      /*  canvas.save();
        paint.setColor(Color.BLUE);
       // canvas.rotate((int) Math.floor(360.0f * 1) - 1+180, cx, cy);//旋转到最低点
        // canvas.drawArc(rectF, -90f, 180f, true, endPaint);
        canvas.drawArc(rectF, -90f, 180f, true, paint);
        canvas.drawCircle(rectF.centerX(),rectF.centerY(),8f,paint);*/
        canvas.save();


        canvas.drawOval(rectF, halfRoundPaint);


      //  canvas.restore();


       // canvas.restoreToCount(restore);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        animation = new RotateAnimation(0,359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(duration);
        animation.setRepeatCount(RotateAnimation.INFINITE);//无限循环
        LinearInterpolator lin = new LinearInterpolator();//默认状态是随sdk不同而不同，5.0以上会一快一慢，这里用线性插值器限定其匀速转动
        animation.setInterpolator(lin);
        this.startAnimation(animation);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAnimation();
    }
}
