package xstar.com.kotlintest.widgets

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import xstar.com.kotlintest.R

class DatePickerGroup @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
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
            mShowItemNumber = a.getInteger(R.styleable.DatePickerGroup_showItemNumber, 3)
            a.recycle()
        }
    }
}