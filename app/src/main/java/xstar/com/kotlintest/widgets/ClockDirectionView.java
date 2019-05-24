package xstar.com.kotlintest.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by xstar on 2016-12-01.
 * 表盘太阳方位法
 */
public class ClockDirectionView extends View {
    public static final String TAG = "DirectionView";
    private static final String[] directs = {"北", "东", "南", "西"};

    public ClockDirectionView(Context context) {
        this(context, null);
    }

    public ClockDirectionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private DisplayMetrics displayMetrics = new DisplayMetrics();

    public ClockDirectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        textSize = (int) (displayMetrics.scaledDensity * textSize);
    }

    Paint paint = new Paint();
    //刻度线长度
    private int scale_len = 10;
    private int space = 8;
    private int textSize = 25;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas, scale_len);
        drawDirectText(canvas);
        drawArrow(canvas, scale_len);
        drawSunArrow(canvas, scale_len, new Date());
    }

    private void drawSunArrow(Canvas canvas, int scale_len, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //时取其半对太阳 12点钟指北方
        drawSunArrow(canvas, scale_len, calendar.get(Calendar.HOUR_OF_DAY) / 2, calendar.get(Calendar.MINUTE) / 2);
    }

    private void drawSunArrow(Canvas canvas, int scale_len, int hour, int minute) {
        paint.setStyle(Paint.Style.STROKE);
        int degress = (int) ((hour + minute / 60f) * 30);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int half_w = width >> 1;
        int half_h = height >> 1;
        int tl_half = scale_len >> 1;
        int tl_height = (int) (tl_half * Math.pow(3, 0.5));
        int radius = computeCircle(canvas, 2);
        int start_y = half_h - radius;
        drawArrow(canvas, degress, half_w, start_y, tl_height, scale_len, radius, paint);
    }

    /**
     * @param canvas
     * @param degree    箭头角度
     * @param sx        箭头点x
     * @param sy        箭头点y
     * @param head_h    箭头三角的高
     * @param head_w    箭头三角的宽
     * @param arrow_len 箭头总长度
     */
    private void drawArrow(Canvas canvas, int degree, int sx, int sy, int head_h, int head_w, int arrow_len, Paint paint) {
        canvas.save();
        canvas.rotate(degree, canvas.getWidth() >> 1, canvas.getHeight() >> 1);
        int half_w = head_w >> 1;
        Path path = new Path();
        path.moveTo(sx, sy);
        path.lineTo(sx - half_w, sy + head_h);
        path.lineTo(sx - half_w, sy + arrow_len);
        path.lineTo(sx + half_w, sy + arrow_len);
        path.lineTo(sx + half_w, sy + head_h);
        path.close();
        canvas.drawPath(path, paint);
        canvas.restore();
    }

    private void drawCircle(Canvas canvas, int len) {
        initClockPaint();
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int half_w = width >> 1;
        int half_h = height >> 1;
        float radius = computeCircle(canvas, 1);//圆盘的半径
        for (int i = 0; i < 360; i++) {
            //画刻度之前，先把画布的状态保存下来
            canvas.save();
            //让画布旋转3/5度，参数一是需要旋转的度数，参数2,3是旋转的圆心
            canvas.rotate(i, half_w, half_h);
            //旋转后再圆上画上一长10dp的刻度线
            int l = len;
            if (i % 15 == 0 && i % 30 != 0 && i % 90 != 0) {
                l = 2 * len;
                paint.setStrokeWidth(2);
            } else if (i % 15 == 0 && i % 90 != 0) {
                l = 3 * len;
                paint.setStrokeWidth(2);
            } else if (i % 90 == 0) {
                l = 4 * len;
                paint.setStrokeWidth(3);
            } else {
                paint.setStrokeWidth(2);
            }
            canvas.drawLine(half_w, half_h - radius, half_w, half_h - radius + l, paint);
            //恢复画布
            canvas.restore();
        }
    }

    private void drawArrow(Canvas canvas, int triangle_len) {
        paint.setStyle(Paint.Style.FILL);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int half_w = width >> 1;
        int half_h = height >> 1;
        int tl_half = triangle_len >> 1;
        int tl_height = (int) (tl_half * Math.pow(3, 0.5));
        int radius = computeCircle(canvas, 2);
        int start_y = half_h - radius;
        drawArrow(canvas, 0, half_w, start_y, tl_height, scale_len, radius, paint);
        drawArrow(canvas, 180, half_w, start_y, tl_height, scale_len, radius, paint);
    }

    private void initClockPaint() {
        paint.setAntiAlias(true);//消除锯齿
        paint.setColor(Color.WHITE);//设置圆盘画笔的颜色为红色
        paint.setStyle(Paint.Style.STROKE);//设置画笔的类型为描边
        paint.setStrokeWidth(1);//设置描边宽度
        paint.setAlpha(100);//设置画笔透明度，最高值为255
    }

    private void initClockCrudePaint() {
        paint.setAntiAlias(true);//消除锯齿
        paint.setColor(Color.WHITE);//设置圆盘画笔的颜色为红色
        paint.setStyle(Paint.Style.STROKE);//设置画笔的类型为描边
        paint.setStrokeWidth(3);//设置描边宽度
        paint.setAlpha(200);//设置画笔透明度，最高值为255
    }

    private void drawDirectText(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(textSize);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int half_w = width >> 1;
        int half_h = height >> 1;
        int radius = computeCircle(canvas, 0);
        for (int i = 0; i < directs.length; i++) {
            canvas.save();
            canvas.rotate(i * 90, half_w, half_h);
            canvas.drawText(directs[i], 0, 1, half_w - (textSize / 2), half_h - radius, paint);
            canvas.restore();
        }
    }

    public int getScale_len() {
        return scale_len;
    }

    public void setScale_len(int scale_len) {
        this.scale_len = scale_len;
        invalidate();
    }

    private boolean inRange(int num, int x, int y) {
        if (x > y) {
            int temp = x;
            x = y;
            y = temp;
        }
        return x <= num && y >= num;
    }

    private int computeCircle(Canvas canvas, int floor) {
        int width = canvas.getWidth();
        int half_w = width >> 1;
        int radius = 0;
        switch (floor) {
            case 0:
                radius = computeCircle(canvas, 1);
                radius += space;
                break;
            case 1:
                radius = (int) (half_w * 0.75f);//圆盘的半径
                break;
            case 2:
                radius = computeCircle(canvas, 1);
                radius -= (scale_len * 4 + space);
                break;
            case 3:
                radius = computeCircle(canvas, 2);
                radius -= ((scale_len / 2 * Math.pow(3, 0.5)) + space);
                break;
            case 4:
                radius = computeCircle(canvas, 3);
                radius -= textSize + space;
                break;

        }
        return radius;
    }
}
