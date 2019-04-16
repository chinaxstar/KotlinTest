package xstar.com.kotlintest

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.important_layot.*
import org.jetbrains.anko.db.select
import xstar.com.kotlintest.data.ImportantDay
import xstar.com.kotlintest.recycler.BaseAdapter
import xstar.com.kotlintest.recycler.find
import xstar.com.kotlintest.util.BaseActivity
import xstar.com.kotlintest.util.SimpleRawParse
import xstar.com.kotlintest.util.getDBHelper

class ImportantDayActivity : BaseActivity(R.layout.important_layot) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dayList.adapter = BaseAdapter<ImportantDay> {
            datas=applicationContext.getDBHelper()?.use {
                select(ImportantDay::class.java.simpleName,"time","descripton").parseList(SimpleRawParse<ImportantDay>()).toMutableList() }
            layout = android.R.layout.simple_list_item_1
            bindData = { vh, p ->
                val item=datas!![p]
                vh.find<TextView>(android.R.id.text1).text=item.description
            }
        }
    }
}