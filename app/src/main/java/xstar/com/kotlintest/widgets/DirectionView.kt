package xstar.com.kotlintest.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager

/**
 * Created by xstar on 2016-12-01.
 */
class DirectionView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private val displayMetrics = DisplayMetrics()

    internal var paint = Paint()
    private var text = getDirectDescription(0)
    var scale_len = 35
        set(scale_len) {
            field = scale_len
            invalidate()
        }
    var directAngle = 0
        set(directAngle) {
            field = directAngle
            this.text = getDirectDescription(directAngle)
            invalidate()
        }
    private val space = 8
    private var textSize = 25

    init {
        val windowManager = getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        textSize = (displayMetrics.scaledDensity * textSize).toInt()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCircle(canvas, this.scale_len)
        drawLittleTriangle(canvas, this.scale_len)
        drawDirectText(canvas, this.directAngle, text)
    }

    private fun drawCircle(canvas: Canvas, len: Int) {
        initClockPaint()
        val width = canvas.width
        val height = canvas.height
        val half_w = width shr 1
        val half_h = height shr 1
        val radius = (half_w - 20).toFloat()//圆盘的半径
        for (i in 0..359) {
            //画刻度之前，先把画布的状态保存下来
            canvas.save()
            //让画布旋转3/5度，参数一是需要旋转的度数，参数2,3是旋转的圆心
            canvas.rotate(i.toFloat(), half_w.toFloat(), half_h.toFloat())

            var l = len
            if (i % 15 == 0 && i % 30 != 0 && i % 90 != 0) {
                l = 2 * len
                paint.strokeWidth = 2f
            } else if (i % 15 == 0 && i % 90 != 0) {
                l = 3 * len
                paint.strokeWidth = 2f
            } else if (i % 90 == 0) {
                l = 4 * len
                paint.strokeWidth = 3f
            } else {
                paint.strokeWidth = 2f
            }
            //旋转后再圆上画上一长10dp的刻度线
            canvas.drawLine(half_w.toFloat(), half_h - radius, half_w.toFloat(), half_h - radius + l, paint)
            //恢复画布
            canvas.restore()
        }
    }

    private fun initClockPaint() {
        paint.isAntiAlias = true//消除锯齿
        paint.color = Color.WHITE//设置圆盘画笔的颜色为红色
        paint.style = Paint.Style.STROKE//设置画笔的类型为描边
        paint.strokeWidth = 1f//设置描边宽度
        paint.alpha = 100//设置画笔透明度，最高值为255
    }

    private fun drawLittleTriangle(canvas: Canvas, triangle_len: Int) {
        paint.style = Paint.Style.FILL
        val width = canvas.width
        val height = canvas.height
        val half_w = width shr 1
        val half_h = height shr 1
        val tl_half = triangle_len shr 1
        val tl_height = (tl_half * Math.pow(3.0, 0.5)).toInt()
        val radius = computeCircle(canvas, 1)
        val start_y = half_h - radius - tl_height - space
        val path = Path()
        path.moveTo(half_w.toFloat(), start_y.toFloat())
        path.lineTo((half_w - tl_half).toFloat(), (start_y + tl_height).toFloat())
        path.lineTo((half_w + tl_half).toFloat(), (start_y + tl_height).toFloat())
        path.close()
        canvas.drawPath(path, paint)
    }

    private fun drawDirectText(canvas: Canvas, angle: Int, text: String) {
        paint.style = Paint.Style.FILL
        val width = canvas.width
        val height = canvas.height
        val half_w = width shr 1
        val half_h = height shr 1

        val radius = computeCircle(canvas, 3)
        for (i in 0..3) {
            canvas.save()
            canvas.rotate((i * 90 - angle).toFloat(), half_w.toFloat(), half_h.toFloat())
            canvas.drawText(directs[i], 0, 1, (half_w - textSize / 2).toFloat(), (half_h - radius + textSize).toFloat(), paint)
            canvas.restore()
        }

        val textR = Rect()
        paint.textSize = textSize.toFloat()
        paint.getTextBounds(text, 0, text.length, textR)
        canvas.drawText(text, 0, text.length, (half_w - textR.width() / 2).toFloat(), (half_h + textR.height() / 2).toFloat(), paint)
    }

    private fun drawAngleInfo(angle: Int, canvas: Canvas, cx: Int, cy: Int) {
        var angle = angle
        val textR = Rect()
        while (angle > 90) angle = angle - 90
        text = angle.toString() + "度"
        paint.textSize = (textSize * 0.82).toFloat()
        paint.getTextBounds(text, 0, text.length, textR)
        canvas.drawText(text, 0, text.length, (cx - textR.width() / 2).toFloat(), (cy - textR.height() / 2).toFloat(), paint)
    }

    private fun getDirectDescription(directAngle: Int): String {
        val text: String
        if (inRange(directAngle, 0, 10) || inRange(directAngle, 350, 360))
            text = "正北"
        else if (inRange(directAngle, 10, 80))
            text = "东偏北"
        else if (inRange(directAngle, 80, 100))
            text = "正东"
        else if (inRange(directAngle, 100, 170))
            text = "东偏南"
        else if (inRange(directAngle, 170, 190))
            text = "正南"
        else if (inRange(directAngle, 190, 260))
            text = "西偏南"
        else if (inRange(directAngle, 260, 280))
            text = "正西"
        else if (inRange(directAngle, 280, 350))
            text = "西偏北"
        else
            text = "未知方向"
        return text
    }

    private fun inRange(num: Int, x: Int, y: Int): Boolean {
        var x = x
        var y = y
        if (x > y) {
            val temp = x
            x = y
            y = temp
        }
        return x <= num && y >= num
    }

    private fun computeCircle(canvas: Canvas, floor: Int): Int {
        val width = canvas.width
        val half_w = width shr 1
        var radius = 0
        when (floor) {
            1 -> radius = half_w - 20//圆盘的半径
            2 -> radius = computeCircle(canvas, 1)
            3 -> {
                radius = computeCircle(canvas, 1)
                radius -= this.scale_len * 4 + space
            }
            4 -> {
                radius = computeCircle(canvas, 3)
                radius -= textSize + space
            }
        }//                radius -= (scale_len + space);
        return radius
    }

    companion object {
        val TAG = "DirectionView"
        private val directs = arrayOf("北", "东", "南", "西")
    }
}
