#### 一、前言
>1.我一直想写一篇关于运动的文章,现在总算`千呼万唤始出来`了。  
2.本篇是一个长篇，各位看官自备水果、饮料、花生米，相信会给你会吃的很开心。

##### 先看一下几个效果:(留图镇楼)
###### 1.---疯狂的分裂

![效果1](https://upload-images.jianshu.io/upload_images/9414344-42749030f79eee56.gif?imageMogr2/auto-orient/strip)

###### 2.---粉身碎骨

![粉身碎骨.gif](https://upload-images.jianshu.io/upload_images/9414344-69f9d65c5a0c70de.gif?imageMogr2/auto-orient/strip)

###### 3.---画笔叠合XOR

![画笔叠合XOR.gif](https://upload-images.jianshu.io/upload_images/9414344-4a25ad46a860f51b.gif?imageMogr2/auto-orient/strip)


##### 1.前置知识论述：
>1).何为运动：视觉上看是一个物体在不同的时间轴上表现出不同的物理位置  
2).`位移 = 初位移 + 速度 * 时间` 小学生的知识不多说  
3).`速度 = 初速度 + 加速度 * 时间` 初中生的知识不多说  
4).时间、位移、速度、加速度构成了现代科学的运动体系

##### 2.使用View对运动学的模拟
>1.时间：ValueAnimator的恒定无限执行----模拟时间流，每次刷新间隔，记为：`1U`  
2.位移：物体在屏幕像素位置----模拟世界，每个像素距离记为:`1px`  
3.速度(单位px/U)、加速度(px/U^2)：自定义  
`注意：无论什么语言，只要能够模拟时间与位移，本篇的思想都可以适用，只是语法不同罢了`

##### 3.测试的物体，封装类：

```
/**
 * 作者：张风捷特烈<br/>
 * 时间：2018/11/11 0011:6:13<br/>
 * 邮箱：1981462002@qq.com<br/>
 * 说明：小球封装类
 */
public class Ball {
    public float aX;//加速度X
    public float aY;//加速度Y
    public float vX;//速度X
    public float vY;//速度Y
    public float x;//点位X
    public float y;//点位Y
    public int color;//颜色
    public float r;//半径
}
```

#### 第一节：物体的匀速直线运动：
##### 1.搭建测试View

>开始是一个位于0,0点、x方向速度10、y方向速度0的小球

```
public class RunBall extends View {
    private ValueAnimator mAnimator;//时间流
    private Ball mBall;//小球对象
    private Paint mPaint;//主画笔
    private Point mCoo;//坐标系

    private float defaultR = 20;//默认小球半径
    private int defaultColor = Color.BLUE;//默认小球颜色
    private float defaultVX = 10;//默认小球x方向速度
    private float defaultVY = 0;//默认小球y方向速度

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
        mBall = new Ball();
        mBall.color = defaultColor;
        mBall.r = defaultR;
        mBall.vX = defaultVX;
        mBall.vY = defaultVY;
        mBall.a = defaultA;
        //初始画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //初始化时间流ValueAnimator
        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setRepeatCount(-1);
        mAnimator.setDuration(1000);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                updateBall();//更新小球信息
                invalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mCoo.x, mCoo.y);
        drawBall(canvas, mBall);
        canvas.restore();
    }

    /**
     * 绘制小球
     * @param canvas
     * @param ball
     */
    private void drawBall(Canvas canvas, Ball ball) {
        mPaint.setColor(ball.color);
        canvas.drawCircle(ball.x, ball.y, ball.r, mPaint);
    }

    /**
     * 更新小球
     */
    private void updateBall() {
        //TODO --运动数据都由此函数变换
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mAnimator.start();//开启时间流
                break; 
            case MotionEvent.ACTION_UP:
                mAnimator.pause();//暂停时间流
                break;
        }
        return true;
    }
}
```

---

##### 2.水平运动：
>注：开录屏+模拟器比较卡，加上变成gif，看上去一些卡,真机运行很流畅

![水平移动.gif](https://upload-images.jianshu.io/upload_images/9414344-6a1cb5891e561f17.gif?imageMogr2/auto-orient/strip)

>RunBall#updateBall:只需加一句(也就是`位移 = 初位移 + 速度 * 时间`,这里时间是1U)

```
private void updateBall() {
    mBall.x += mBall.vX;
}
```

---

##### 3.反弹效果:(x大于400反弹)：

>只需反弹时将vX速度取反就行了，和现实一致

![反弹.gif](https://upload-images.jianshu.io/upload_images/9414344-176c58903bb5a60f.gif?imageMogr2/auto-orient/strip)

```
private void updateBall() {
    mBall.x += mBall.vX;
    if (mBall.x > 400) {
        mBall.vX = -mBall.vX;
    }
}
```

---

##### 4.反弹变色，无限循环：


![反弹变色.gif](https://upload-images.jianshu.io/upload_images/9414344-ac54377c844f9b28.gif?imageMogr2/auto-orient/strip)


```
/**
 * 更新小球
 */
private void updateBall() {
    mBall.x += mBall.vX;
    if (mBall.x > 400) {
        mBall.vX = -mBall.vX;
        mBall.color = ColUtils.randomRGB();//更改颜色
    }
    if (mBall.x < -400) {
        mBall.vX = -mBall.vX;
        mBall.color = ColUtils.randomRGB();//更改颜色
    }
}
```

---

##### 5.小球的箱式弹跳：
>X轴的平移和Y轴的平移基本一致，就不说了，看一下x,y都改变，即速度斜向的情况

![速度的合成.png](https://upload-images.jianshu.io/upload_images/9414344-f9218d61226e9ee7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![碰撞分析png](https://upload-images.jianshu.io/upload_images/9414344-cff4c4d8fc616e16.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![箱子弹跳.gif](https://upload-images.jianshu.io/upload_images/9414344-8a35eac8ace03823.gif?imageMogr2/auto-orient/strip)

>先把边界值定义一下：以便复用

```
private float defaultVY = 5;//默认小球y方向速度

private float mMaxX = 400;//X最大值
private float mMinX = -400;//X最小值
private float mMaxY = 300;//Y最大值
private float mMinY = -100;//Y最小值
```

>现在updateBall方法里添加对Y方向的修改：

```
/**
 * 更新小球
 */
private void updateBall() {
    mBall.x += mBall.vX;
    mBall.y += mBall.vY;
    if (mBall.x > mMaxX) {
        mBall.vX = -mBall.vX;
        mBall.color = ColUtils.randomRGB();//更改颜色
    }
    if (mBall.x < mMinX) {
        mBall.vX = -mBall.vX;
        mBall.color = ColUtils.randomRGB();//更改颜色
    }
    if (mBall.y > mMaxY) {
        mBall.vY = -mBall.vY;
        mBall.color = ColUtils.randomRGB();//更改颜色
    }
    if (mBall.y < mMinY) {
        mBall.vY = -mBall.vY;
        mBall.color = ColUtils.randomRGB();//更改颜色
    }
}
```

>没错,就是这么简单，匀速运动做成这样就差不多了，下面看变速运动


---

#### 二、变速运动

##### 1.自由落体
>首先模拟我们最熟悉的自由落体，加速度aY = 0.98f，x，y初速度为0,初始y高度设为-400

![自由落体.gif](https://upload-images.jianshu.io/upload_images/9414344-004407e4bbfb7e9c.gif?imageMogr2/auto-orient/strip)

```
private float defaultR = 20;//默认小球半径
private int defaultColor = Color.BLUE;//默认小球颜色
private float defaultVX = 0;//默认小球x方向速度
private float defaultVY = 0;//默认小球y方向速度
private float defaultAY = 0.98f;//默认小球加速度
private float mMaxY = 0;//Y最大值
```

>updateBall里根据竖直加速度aY动态改变vY即可,这里反弹之后依然会遵循物理定律  
注意：你可以在反弹是乘个系数当做损耗值，更能模拟现实

```
private void updateBall() {
    mBall.x += mBall.vX;
    mBall.y += mBall.vY;
    mBall.vY += mBall.aY;
    if (mBall.y > mMaxY - mBall.r) {
        mBall.vY = -mBall.vY;
        mBall.color = ColUtils.randomRGB();//更改颜色
    }
}
```

---

##### 2.平抛运动+模拟碰撞损耗
>平抛也就是有一个初始的x方向速度的自由落体

![平抛运动+模拟碰撞损耗.gif](https://upload-images.jianshu.io/upload_images/9414344-b5ba3a47ee282972.gif?imageMogr2/auto-orient/strip)

>修改初始水平速度和碰撞损耗系数

```
private float defaultVX = 15;//默认小球x方向速度
private float defaultF = 0.9f;//碰撞损耗
```


```
/**
 * 更新小球
 */
private void updateBall() {
    mBall.x += mBall.vX;
    mBall.y += mBall.vY;
    mBall.vY += mBall.aY;
    if (mBall.x > mMaxX) {
        mBall.x = mMaxX;
        mBall.vX = -mBall.vX * defaultF;
        mBall.color = ColUtils.randomRGB();//更改颜色
    }
    if (mBall.x < mMinX) {
        mBall.x = mMinX;
        mBall.vX = -mBall.vX * defaultF;
        mBall.color = ColUtils.randomRGB();//更改颜色
    }
    if (mBall.y > mMaxY) {
        mBall.y = mMaxY;
        mBall.vY = -mBall.vY * defaultF;
        mBall.color = ColUtils.randomRGB();//更改颜色
    }
    if (mBall.y < mMinY) {
        mBall.y = mMinY;
        mBall.vY = -mBall.vY * defaultF;
        mBall.color = ColUtils.randomRGB();//更改颜色
    }
}
```

---

##### 3.斜抛运动：具有初始水平和垂直速度

![斜抛运动.gif](https://upload-images.jianshu.io/upload_images/9414344-37bff5296b138237.gif?imageMogr2/auto-orient/strip)

>修改一下初始垂直速度即可

```
private float defaultVY = -12;//默认小球y方向速度
```

---

##### 5.圆周运动：
>可惜我无法用运动学模拟，需要合速度和合加速度保持不垂直，并且合加速度不变。看以后能不能实现  
不过退而求其次，用画布的旋转可以让小球做圆周运动  
mark：ValueAnimator默认Interpolator竟然不是线性的，怪不得看着怪怪的  

![圆周运动.gif](https://upload-images.jianshu.io/upload_images/9414344-6f3d6dd153912f3f.gif?imageMogr2/auto-orient/strip)

```
//初始化时间流ValueAnimator
mAnimator = ValueAnimator.ofFloat(0, 1);
mAnimator.setRepeatCount(-1);
mAnimator.setDuration(4000);
mAnimator.setInterpolator(new LinearInterpolator());
mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        mDeg = (float) animation.getAnimatedValue() * 360;
        updateBall();//更新小球位置
        invalidate();
    }
});

@Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.save();
    canvas.translate(mCoo.x, mCoo.y);
    canvas.rotate(mDeg+90);
    canvas.drawLine(0, 0, mBall.x, mBall.y, mPaint);
    drawBall(canvas, mBall);
    canvas.restore();
}
```

---
##### 6.钟摆运动：
>也是非运动学的钟摆,通过旋转画布模拟：

![钟摆.gif](https://upload-images.jianshu.io/upload_images/9414344-663d36e8d424404c.gif?imageMogr2/auto-orient/strip)

```
//初始化时间流ValueAnimator
mAnimator = ValueAnimator.ofFloat(0, 1);
mAnimator.setRepeatCount(-1);
mAnimator.setDuration(2000);
mAnimator.setRepeatMode(ValueAnimator.REVERSE);
mAnimator.setInterpolator(new LinearInterpolator());
mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        mDeg = (float) animation.getAnimatedValue() * 360*0.5f;
        updateBall();//更新小球位置
        invalidate();
    }
});
```

#### 三、效果实现

##### 1.碰撞分裂的效果实现

![粉身碎骨.gif](https://upload-images.jianshu.io/upload_images/9414344-69f9d65c5a0c70de.gif?imageMogr2/auto-orient/strip)

>思路：由绘制一个小球到绘制一个小球集合，每当碰撞时在集合里添加一个反向的小球  
并将两个小球半径都减半即可，还是好理解的。

```
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
                ball.color = ColUtils.randomRGB();//更改颜色
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
                ball.color = ColUtils.randomRGB();

                ball.r = ball.r / 2;
            }
            if (ball.y > mMaxY) {

                ball.y = mMaxY;
                ball.vY = -ball.vY * defaultF;
                ball.color = ColUtils.randomRGB();
            }
            if (ball.y < mMinY) {
                ball.y = mMinY;
                ball.vY = -ball.vY * defaultF;
                ball.color = ColUtils.randomRGB();
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
}

```


##### Ball.java：加一个浅拷贝

```
public class Ball implements Cloneable {
    public float aX;//加速度
    public float aY;//加速度Y
    public float vX;//速度X
    public float vY;//速度Y
    public float x;//点位X
    public float y;//点位Y
    public int color;//颜色
    public float r;//半径


    public Ball clone() {
        Ball clone = null;
        try {
            clone = (Ball) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }
}
```

---
##### 2.画笔叠合XOR测试：

![画笔叠合XOR.gif](https://upload-images.jianshu.io/upload_images/9414344-4a25ad46a860f51b.gif?imageMogr2/auto-orient/strip)

```
//初始化时准备一个小球数组---参数值随机一些
private void initBalls() {
    for (int i = 0; i < 28; i++) {
        Ball mBall = new Ball();
        mBall.color = ColUtils.randomRGB();
        mBall.r = rangeInt(80, 120);
        mBall.vX = (float) (Math.pow(-1, Math.ceil(Math.random() * 1000)) * 20 * Math.random());
        mBall.vY = rangeInt(-15, 35);
        mBall.aY = 0.98f;
        mBall.x = 0;
        mBall.y = 0;
        mBalls.add(mBall);
    }
}

@Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    //创建一个图层，在图层上演示图形混合后的效果
    int sc = 0;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
        sc = canvas.saveLayer(new RectF(0, 0, 2500, 2500), null);
    }
    mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));//设置对源的叠合模式
    canvas.translate(mCoo.x, mCoo.y);
    drawBalls(canvas, mBalls);
    canvas.restoreToCount(sc);
}
```

---
##### 3.两个小球的碰撞反弹

![两个小球的碰撞反弹.gif](https://upload-images.jianshu.io/upload_images/9414344-1f31127f22cd7bf1.gif?imageMogr2/auto-orient/strip)


```
//准备两个球
private void initBalls() {
    for (int i = 0; i < 2; i++) {
        Ball mBall = new Ball();
        mBall.color = Color.RED;
        mBall.r = 80;
        mBall.vX = (float) (Math.pow(-1, Math.ceil(Math.random() * 1000)) * 20 * Math.random());
        mBall.vY = rangeInt(-15, 35);
        mBall.aY = 0.98f;
        mBalls.add(mBall);
    }
    mBalls.get(1).x = 300;
    mBalls.get(1).y = 300;
    mBalls.get(1).color = Color.BLUE;
}

/**
 * 两点间距离函数
 */
public static float disPos2d(float x1, float y1, float x2, float y2) {
    return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
}

/**
 * 更新小球
 */
private void updateBall() {
    Ball redBall = mBalls.get(0);
    Ball blueBall = mBalls.get(1);
    //校验两个小球的距离
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

```

>好了，就到这里，关于View的运动还有很多可变化的东西，有兴趣的可以去探索一些


---

#### 后记：捷文规范
##### 1.本文成长记录及勘误表
项目源码 | 日期|备注
---|---|---
V0.1--无|2018-11-15|[Android原生绘图之让你了解View的运动](https://www.jianshu.com/p/4440eb9a9e56)

##### 2.更多关于我

笔名 | QQ|微信|爱好
---|---|---|---|
张风捷特烈 | 1981462002|zdl1994328|语言
 [我的github](https://github.com/toly1994328)|[我的简书](https://www.jianshu.com/u/e4e52c116681)|[我的CSDN](https://blog.csdn.net/qq_30447263)|[个人网站](http://www.toly1994.com)

##### 3.声明
>1----本文由张风捷特烈原创,转载请注明  
2----欢迎广大编程爱好者共同交流  
3----个人能力有限，如有不正之处欢迎大家批评指证，必定虚心改正   
4----看到这里，我在此感谢你的喜欢与支持