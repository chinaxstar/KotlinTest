package xstar.com.kotlintest

import android.os.Bundle
import android.text.format.DateUtils
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import kotlinx.android.synthetic.main.important_layot.*
import kotlinx.coroutines.delay
import org.jetbrains.anko.*
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import xstar.com.kotlintest.data.ImportantDay
import xstar.com.kotlintest.recycler.BaseAdapter
import xstar.com.kotlintest.recycler.RxView
import xstar.com.kotlintest.recycler.find
import xstar.com.kotlintest.util.*
import java.util.*

class ImportantDayActivity : BaseActivity(R.layout.important_layot) {

    val daysMillis=24*3600*1000
    val adapter = BaseAdapter<ImportantDay> {
        itemLayout = R.layout.important_days_list_item
        bindData = { vh, p ->
            val item = datas!![p]
            vh.find<TextView>(R.id.title).text = item.description
            vh.find<TextView>(R.id.startTime).text = Date(item.time).format("yyyy年MM月dd日")
            vh.find<TextView>(R.id.days).text = "${(System.currentTimeMillis()-item.time)/daysMillis} 天"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter.datas = getData()
        dayList.layoutManager = LinearLayoutManager(this@ImportantDayActivity)
        dayList.adapter = adapter
        val dispose = RxView.onClick(add).subscribe {
            alert {
                title = "纪念日设置"
                val view = inflate(R.layout.add_important_day_layout, null, false)
                customView = view
                val title = view.find<EditText>(R.id.title)
                val date = view.find<TextView>(R.id.date)

                RxView.onClick(date).subscribe {
                    showSelectDay(date)
                }
                okButton {
                    if (title.text.isNullOrEmpty()) {
                        toast("标题为空！")
                        return@okButton
                    }
                    if (date.text.isNullOrEmpty()) {
                        toast("标题为空！")
                        return@okButton
                    }

                    applicationContext.getDBHelper()?.use {
                        val importantDay = ImportantDay(date.text.toString().parseDate("yyyy年M月d日").time, title.text.toString())
                        insert(ImportantDay::class.java.simpleName, *getObjColumnsAndValue(importantDay))
                        adapter.datas = getData()
                        adapter.notifyDataSetChanged()
                    }
                }
                cancelButton {
                    it.dismiss()
                }
            }.show()
        }
        addDispose(dispose)
    }


    fun getData(): MutableList<ImportantDay>? {
        return applicationContext.getDBHelper()?.use {
            val clzz=ImportantDay::class.java
            select(clzz.simpleName, *getObjColumnNames(clzz)).parseList(SimpleRawParse(clzz)).toMutableList()
        }
    }

    fun showSelectDay(date: TextView) {
        lateinit var calendarView: CalendarView
        val alertDialog = alert {
            title = "选择日期"
            val view = inflate(R.layout.select_date_layout, null, false)
            customView = view
            calendarView = view.find(R.id.calendarView)
        }.show()
        calendarView.setOnCalendarSelectListener(object : CalendarView.OnCalendarSelectListener {
            override fun onCalendarSelect(calendar: Calendar?, isClick: Boolean) {
                if (isClick) {
                    val dateStr = "${calendar?.year}年${calendar?.month}月${calendar?.day}日"
                    date.text = dateStr
                    alertDialog.dismiss()
                }
            }

            override fun onCalendarOutOfRange(calendar: Calendar?) {

            }
        })
    }
}