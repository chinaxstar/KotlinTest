package xstar.com.kotlintest

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import org.jetbrains.anko.*
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import xstar.com.kotlintest.data.ImportantDay
import xstar.com.kotlintest.recycler.BaseAdapter
import xstar.com.kotlintest.recycler.RxView
import xstar.com.kotlintest.recycler.find
import xstar.com.kotlintest.util.*

class ImportantDayActivity : BaseActivity(R.layout.important_layot) {

    val adapter=BaseAdapter<ImportantDay> {
        layout = android.R.layout.simple_list_item_1
        bindData = { vh, p ->
            val item = datas!![p]
            vh.find<TextView>(android.R.id.text1).text = item.description
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter.datas=getData()
        val dayList=find<RecyclerView>(R.id.dayList)
        val add=find<FloatingActionButton>(R.id.add)
        dayList.layoutManager=LinearLayoutManager(this)
        dayList.adapter = adapter
        val dispose = RxView.onClick(add).subscribe {
            alert {
                title = "纪念日设置"
                val view = inflate(R.layout.add_important_day_layout,null,false)
                customView = view
                val _title = view.find<EditText>(R.id.title)
                val date = view.find<TextView>(R.id.date)

                RxView.onClick(date).subscribe {
                    showSelectDay(date)
                }
                okButton {
                    if (_title.text.isNullOrEmpty()) {
                        toast("标题为空！")
                        return@okButton
                    }
                    if (date.text.isNullOrEmpty()) {
                        toast("标题为空！")
                        return@okButton
                    }

                    applicationContext.getDBHelper()?.use {
                        val importantDay=ImportantDay(date.text.toString().parseDate("yyyy年M月d日").time,_title.text.toString())
                        insert(ImportantDay::class.java.simpleName,*getObjColumns(importantDay))
                        adapter.datas=getData()
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

    fun getData():MutableList<ImportantDay>?{
        return applicationContext.getDBHelper()?.use {
            select(ImportantDay::class.java.simpleName, "time", "description").parseList(SimpleRawParse(ImportantDay::class.java)).toMutableList()
        }
    }
    fun showSelectDay(date:TextView){
        lateinit var calendarView:CalendarView
        val alertDialog= alert {
            title="选择日期"
            val view=inflate(R.layout.select_date_layout,null,false)
            customView=view
            calendarView=view.find(R.id.calendarView)
        }.show()
        calendarView.setOnCalendarSelectListener(object :CalendarView.OnCalendarSelectListener{
            override fun onCalendarSelect(calendar: Calendar?, isClick: Boolean) {
                if (isClick) {
                    val dateStr="${calendar?.year}年${calendar?.month}月${calendar?.day}日"
                    date.text=dateStr
                    alertDialog.dismiss()
                }
            }

            override fun onCalendarOutOfRange(calendar: Calendar?) {

            }
        })
    }
}