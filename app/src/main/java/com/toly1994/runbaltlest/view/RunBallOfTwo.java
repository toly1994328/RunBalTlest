package com.toly1994.runbaltlest.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：张风捷特烈<br/>
 * 时间：2018/11/15 0015:8:10<br/>
 * 邮箱：1981462002@qq.com<br/>
 * 说明：小球运动测试
 */
public class RunBallOfTwo extends View {
    private ValueAnimator mAnimator;//时间流

    private List<Ball> mBalls;//小球对象
    private Paint mPaint;//主画笔
    private Paint mHelpPaint;//辅助线画笔
    private Point mCoo;//坐标系

    private float defaultF = 0.97f;//碰撞损耗

    private float mMaxX;//X最大值
    private float mMinX;//X最小值
    private float mMaxY;//Y最大值
    private float mMinY;//Y最小值
    private PorterDuffXfermode[] mModes;
    private Point mWinSize;
    private Ball mBall;

    public RunBallOfTwo(Context context) {
        this(context, null);
    }

    public RunBallOfTwo(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        mCoo = new Point(20, 300);

        mWinSize = new Point();


        loadWinSize(getContext(), mWinSize);
        mMaxX = mWinSize.x - mCoo.x - 50;
        mMinX = -mCoo.x + 50;
        mMaxY = mWinSize.y - mCoo.y - 50;
        mMinY = -mCoo.y + 50;

        mBall = new Ball();

        //初始画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBalls = new ArrayList<>();
        initBalls();

        mHelpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHelpPaint.setColor(Color.BLACK);
        mHelpPaint.setStyle(Paint.Style.FILL);
        mHelpPaint.setStrokeWidth(3);

        //初始化时间流ValueAnimator

        Ball startBall = new Ball();
        startBall.color = Color.RED;
        startBall.r = 20;

        Ball endBall = startBall.clone();
        endBall.x = 1800;
        endBall.y = 300;


        mAnimator = ValueAnimator.ofObject(new SinEvaluator(), startBall, endBall);

        mAnimator.setRepeatCount(-1);
        mAnimator.setDuration(8000);
        mAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(animation -> {

            mBall = (Ball) animation.getAnimatedValue();
            invalidate();
//            updateBall();//更新小球位置
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mCoo.x, mCoo.y);

        canvas.drawLine(0,0,1800,0,mPaint);
        canvas.drawLine(0,100,1800,100,mPaint);
        canvas.drawLine(0,-100,1800,-100,mPaint);
        drawBall(canvas, mBall);
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
     * 绘制小球集合
     *
     * @param canvas
     * @param ball   小球集合
     */
    private void drawBall(Canvas canvas, Ball ball) {
        mPaint.setColor(ball.color);
        canvas.drawCircle(ball.x, ball.y, ball.r, mPaint);
    }

    /**
     * 更新小球
     */
    private void updateBall() {
        Ball redBall = mBalls.get(0);
        Ball blueBall = mBalls.get(1);

        if (disPos2d(redBall.x, redBall.y, blueBall.x, blueBall.y) < 80 * 2) {
            redBall.vX = -redBall.vX;
            redBall.vY = -redBall.vY;
            blueBall.vX = -blueBall.vX;
            blueBall.vY = -blueBall.vY;
        }


        for (int i = 0; i < mBalls.size(); i++) {
            Ball ball = mBalls.get(i);
            ball.x += ball.vX;
            ball.y += ball.vY;
            ball.vY += ball.aY;
            ball.vX += ball.aX;

            if (ball.x > mMaxX) {
                ball.x = mMaxX;
                ball.vX = -ball.vX * defaultF;
            }
            if (ball.x < mMinX) {

                ball.x = mMinX;
                ball.vX = -ball.vX * defaultF;

            }
            if (ball.y > mMaxY) {

                ball.y = mMaxY;
                ball.vY = -ball.vY * defaultF;
            }
            if (ball.y < mMinY) {
                ball.y = mMinY;
                ball.vY = -ball.vY * defaultF;
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

    private void initBalls() {
        Ball startBall = new Ball();
        startBall.color = Color.RED;
        startBall.r = 20;
        startBall.vX = (float) (Math.pow(-1, Math.ceil(Math.random() * 1000)) * 20 * Math.random());
        startBall.vY = rangeInt(-15, 35);
        startBall.aY = 0.98f;


        Ball endBall = startBall.clone();
        endBall.x = 2000;
        endBall.y = 2000;

//        mBalls.add(startBall);
//        mBalls.get(1).x = 2000;
//        mBalls.get(1).y = 300;
//        mBalls.get(1).color = Color.BLUE;
    }

    /**
     * 两点间距离函数
     */
    public static float disPos2d(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }


    /**
     * 获取范围随机整数：如 rangeInt(1,9)
     *
     * @param s 前数(包括)
     * @param e 后数(包括)
     * @return 范围随机整数
     */
    public static int rangeInt(int s, int e) {
        int max = Math.max(s, e);
        int min = Math.min(s, e) - 1;
        return (int) (min + Math.ceil(Math.random() * (max - min)));
    }

    /**
     * 获得屏幕高度
     *
     * @param ctx     上下文
     * @param winSize 屏幕尺寸
     */
    public static void loadWinSize(Context ctx, Point winSize) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(outMetrics);
        }
        winSize.x = outMetrics.widthPixels;
        winSize.y = outMetrics.heightPixels;
    }
}
