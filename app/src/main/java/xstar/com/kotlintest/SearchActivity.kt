package xstar.com.kotlintest

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import xstar.com.kotlintest.apis.HttpMethods
import xstar.com.kotlintest.constant.C
import xstar.com.kotlintest.data.IDInfo
import xstar.com.kotlintest.data.JuHeRep
import xstar.com.kotlintest.data.SimpleSubscribe
import xstar.com.kotlintest.recycler.BaseAdapter
import xstar.com.kotlintest.recycler.BaseVH
import xstar.com.kotlintest.recycler.OnItemClickListener
import xstar.com.kotlintest.recycler.find
import xstar.com.kotlintest.util.BaseActivity

class SearchActivity : BaseActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findIDCard(search_server_et.text.toString())
        app_recommend_tv.layoutManager = LinearLayoutManager(this)
        rvAdapter.onItemClickListner = object : OnItemClickListener<Int> {
            override fun onItemClick(holder: BaseVH, position: Int, item: Int) {
                if (position == 0) findIDCard(search_server_et.text.toString())
            }
        }
        app_recommend_tv.adapter = rvAdapter
        search_server_et.addTextChangedListener(textChangeListener)
    }

    val textChangeListener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            analyzeText(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    fun analyzeText(str: String) {
        val len: Int = str.length
        val result = when (len) {
            in 15..18 -> intArrayOf(C.ID_CARD)
            in 6..11 -> intArrayOf(C.QQ_NUMBER)
            else -> kotlin.IntArray(0)
        }
        rvAdapter.datas = result.asList().toMutableList()
        rvAdapter.notifyDataSetChanged()
    }

    val rvAdapter = BaseAdapter<Int> {
        itemLayout = android.R.layout.simple_list_item_1
        bindData = { h, p ->
            val item = datas!![p]
            h.itemView.setOnClickListener { onItemClickListner?.onItemClick(h, p, itemLayout) }
            val result = when (item) {
                0 -> "身份证查询"
                else -> "未查询到结果"
            }
            h.find<TextView>(android.R.id.text1).text = result
        }
    }

    fun findIDCard(search: String) {
        val api = HttpMethods.findIDInfo(search, object : SimpleSubscribe<JuHeRep<IDInfo>>() {
            override fun onNext(t: JuHeRep<IDInfo>) {
                Log.e("idInfo", HttpMethods.gson.toJson(t))
            }
        })
    }
}
