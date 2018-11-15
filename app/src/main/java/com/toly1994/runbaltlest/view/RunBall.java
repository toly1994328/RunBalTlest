package com.toly1994.runbaltlest.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 作者：张风捷特烈<br/>
 * 时间：2018/11/15 0015:8:10<br/>
 * 邮箱：1981462002@qq.com<br/>
 * 说明：小球运动测试
 */
public class RunBall extends View {
    private ValueAnimator mAnimator;//时间流

    private List<Ball> mBalls;//小球对象
    private Paint mPaint;//主画笔
    private Paint mHelpPaint;//辅助线画笔
    private Point mCoo;//坐标系

    private float defaultR = 80;//默认小球半径
    private int defaultColor = Color.BLUE;//默认小球颜色
    private float defaultVX = 10;//默认小球x方向速度
    private float defaultF = 0.95f;//碰撞损耗
    private float defaultVY = 0;//默认小球y方向速度
    private float defaultAY = 0.5f;//默认小球加速度

    private float mMaxX = 600;//X最大值
    private float mMinX = -200;//X最小值
    private float mMaxY = 300;//Y最大值
    private float mMinY = -100;//Y最小值

    public RunBall(Context context) {
        this(context, null);
    }

    public RunBall(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mCoo = new Point(500, 500);
        //初始化小球

        //初始画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBalls = new ArrayList<>();
        Ball ball = initBall();
        mBalls.add(ball);

        mHelpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHelpPaint.setColor(Color.BLACK);
        mHelpPaint.setStyle(Paint.Style.FILL);
        mHelpPaint.setStrokeWidth(3);

        //初始化时间流ValueAnimator
        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setRepeatCount(-1);
        mAnimator.setDuration(2000);
        mAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(animation -> {
            updateBall();//更新小球位置
            invalidate();
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mCoo.x, mCoo.y);
        drawBalls(canvas, mBalls);
        canvas.restore();
    }

    /**
     * 绘制小球集合
     *
     * @param canvas
     * @param balls  小球集合
     */
    private void drawBalls(Canvas canvas, List<Ball> balls) {
        for (Ball ball : balls) {
            mPaint.setColor(ball.color);
            canvas.drawCircle(ball.x, ball.y, ball.r, mPaint);
        }
    }

    /**
     * 更新小球
     */
    private void updateBall() {
        for (int i = 0; i < mBalls.size(); i++) {
            Ball ball = mBalls.get(i);
            if (ball.r < 1) {//帮半径小于1就移除
                mBalls.remove(i);
            }
            ball.x += ball.vX;
            ball.y += ball.vY;
            ball.vY += ball.aY;
            ball.vX += ball.aX;
            if (ball.x > mMaxX) {
                Ball newBall = ball.clone();//新建一个ball同等信息的球
                newBall.r = newBall.r / 2;
                newBall.vX = -newBall.vX;
                newBall.vY = -newBall.vY;
                mBalls.add(newBall);

                ball.x = mMaxX;
                ball.vX = -ball.vX * defaultF;
                ball.color =randomRGB();//更改颜色
                ball.r = ball.r / 2;
            }
            if (ball.x < mMinX) {
                Ball newBall = ball.clone();
                newBall.r = newBall.r / 2;
                newBall.vX = -newBall.vX;
                newBall.vY = -newBall.vY;
                mBalls.add(newBall);

                ball.x = mMinX;
                ball.vX = -ball.vX * defaultF;
                ball.color =randomRGB();

                ball.r = ball.r / 2;
            }
            if (ball.y > mMaxY) {

                ball.y = mMaxY;
                ball.vY = -ball.vY * defaultF;
                ball.color =randomRGB();
            }
            if (ball.y < mMinY) {
                ball.y = mMinY;
                ball.vY = -ball.vY * defaultF;
                ball.color =randomRGB();
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mAnimator.start();
                break;
            case MotionEvent.ACTION_UP:
//                mAnimator.pause();
                break;
        }
        return true;
    }

    private Ball initBall() {
        Ball mBall = new Ball();
        mBall.color = defaultColor;
        mBall.r = defaultR;
        mBall.vX = defaultVX;
        mBall.vY = defaultVY;
        mBall.aY = defaultAY;
        mBall.x = 0;
        mBall.y = 0;
        return mBall;
    }


    /**
     * 返回随机颜色
     *
     * @return 随机颜色
     */
    public static int randomRGB() {
        Random random = new Random();
        int r = 30 + random.nextInt(200);
        int g = 30 + random.nextInt(200);
        int b = 30 + random.nextInt(200);
        return Color.rgb( r, g, b);
    }
}
