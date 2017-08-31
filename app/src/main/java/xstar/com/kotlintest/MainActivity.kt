package xstar.com.kotlintest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import xstar.com.kotlintest.apis.HttpMethods
import xstar.com.kotlintest.constant.C
import xstar.com.kotlintest.recycler.BaseAdapter
import xstar.com.kotlintest.recycler.BaseVH

class MainActivity : AppCompatActivity() {
    val json = "{\"resultcode\":\"201\",\"reason\":\"请输入正确的15或18位身份证\",\"result\":null,\"error_code\":203801}"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findIDCard(search_server_et.text.toString())
        app_recommend_tv.layoutManager=LinearLayoutManager(this)
        rvAdapter.onItemClickListener = itemClick
        app_recommend_tv.adapter=rvAdapter

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
        rvAdapter.itemList = result.asList()
        rvAdapter.notifyDataSetChanged()
    }

    val rvAdapter = object : BaseAdapter<Int>() {
        override fun onBindViewHolder(holder: BaseVH?, position: Int) {
            holder?.itemView?.setOnClickListener { onItemClickListener?.onItemClick(this,it,position) }
            val item = itemList!![position]
            val result = when (item) {
                0 -> "身份证查询"
                else -> "未查询到结果"
            }
            holder?.find<TextView>(android.R.id.text1)?.setText(result)
        }

        init {
            layout = android.R.layout.simple_list_item_1
        }

    }
    val http = HttpMethods()
    val itemClick = object:BaseAdapter.OnItemClickListener {
        override fun onItemClick(adapter: RecyclerView.Adapter<BaseVH>, view: View, position: Int) {
            if (position == 0) findIDCard(search_server_et.text.toString())
        }
    }

    fun findIDCard(search: String) {
        val api = http.apiService.getIdCardInfo(C.JUHE_KEY, search)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { data -> Log.e("json", http.gson.toJson(data)) }
                .doOnError { e -> Log.e("errot", e.message) }
                .subscribe()
    }
}
