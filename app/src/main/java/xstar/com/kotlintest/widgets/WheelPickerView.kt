package xstar.com.kotlintest.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.util.Pools
import xstar.com.kotlintest.R

/**
 * 轮式选择器
 */
class WheelPickerView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var mContext = context

    var mTextSize = 32f
    var mTextColor = Color.parseColor("#ffeeeeee")
    var mItemPaddingLeftAndRight = 10f
    var mItemPaddingTopAndBottom = 10f
    var mShowItemNumber = 3

    init {
        attrs?.let {
            val a = mContext.obtainStyledAttributes(it, R.styleable.DatePickerGroup, defStyleAttr, 0)
            mTextSize = a.getDimension(R.styleable.DatePickerGroup_textSize, 32f)
            mTextColor = a.getColor(R.styleable.DatePickerGroup_textColor, mTextColor)
            mItemPaddingLeftAndRight = a.getDimension(R.styleable.DatePickerGroup_itemPaddingLeftAndRight, 10f)
            mItemPaddingTopAndBottom = a.getDimension(R.styleable.DatePickerGroup_itemPaddingTopBottom, 10f)
            mItemPaddingTopAndBottom = a.getDimension(R.styleable.DatePickerGroup_itemPaddingTopBottom, 10f)
            mShowItemNumber = a.getInteger(R.styleable.DatePickerGroup_showItemNumber, 3)
            a.recycle()
        }
    }

    private var pools = Pools.SynchronizedPool<ItemHolder>(15)


    private class ItemHolder {
        /**
         * 内容
         */
        var itemText = ""
        /**
         * x坐标
         */
        var x = 0f
        /**
         * y坐标
         */
        var y = 0f
        /**
         * 移动距离
         */
        var move = 0f
        /**
         * 字体画笔
         */
        private var textPaint: Paint? = null
        /**
         * 字体范围矩形
         */
        private var textRect: Rect =  Rect()

        /**
         * 绘制自身
         *
         * @param canvas
         */
        fun drawSelf(canvas: Canvas) {

            if (textPaint == null) {
                textPaint = Paint()
                textPaint!!.isAntiAlias = true
            }

            if (textRect == null)
                textRect = Rect()



        }

        /**
         * 是否在可视界面内
         *
         * @return
         */
        fun isInView(): Boolean {
            return true
        }

        /**
         * 移动距离
         *
         * @param _move
         */
        fun move(_move: Int) {
            this.move = _move.toFloat()
        }

        /**
         * 设置新的坐标
         *
         * @param _move
         */
        fun newY(_move: Int) {
            this.move = 0f
            this.y = y + _move
        }

        /**
         * 判断是否在选择区域内
         *
         * @return
         */
        fun isSelected(): Boolean {
            return  false
        }

        /**
         * 获取移动到标准位置需要的距离
         */
        fun moveToSelected(): Float {
            return 0f
        }
    }
}