package xstar.com.kotlintest

import android.databinding.DataBindingUtil.bind
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import xstar.com.kotlintest.apis.HttpMethods
import xstar.com.kotlintest.constant.C
import xstar.com.kotlintest.data.Response
import xstar.com.kotlintest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val json = "{\"resultcode\":\"201\",\"reason\":\"请输入正确的15或18位身份证\",\"result\":null,\"error_code\":203801}"
    val layoutManager: LinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    lateinit var binder: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflate: LayoutInflater = LayoutInflater.from(this)
        val rootview: View = inflate.inflate(R.layout.activity_main, null, false)
        binder = bind(rootview)
        setContentView(rootview)
        binder.searchServerEt.addTextChangedListener(textChangeListener)
        binder.appRecommendTv.layoutManager = layoutManager
        rvAdapter.onItemClickListener = itemClick
        binder.appRecommendTv.adapter = rvAdapter
        findIDCard()
        val re = http.gson.fromJson(json, Response::class.java)
        val data = re.result
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
        rvAdapter.data.clear()
        rvAdapter.data.addAll(result.asList())
        rvAdapter.notifyDataSetChanged()
    }

    val rvAdapter = object : BaseQuickAdapter<Int, BaseViewHolder>(android.R.layout.simple_list_item_1) {
        override fun convert(helper: BaseViewHolder?, item: Int?) {
            val result = when (item) {
                0 -> "身份证查询"
                else -> "未查询到结果"
            }
            helper?.setText(android.R.id.text1, result)
        }
    }
    val http = HttpMethods()
    val itemClick = BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
        if (position == 0) findIDCard()
    }

    fun findIDCard() {
        val api = http.apiService.getIdCardInfo(C.JUHE_KEY, binder.searchServerEt.editableText.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { data -> Log.e("json", http.gson.toJson(data)) }
                .doOnError { e -> Log.e("errot", e.message) }
                .subscribe()
    }
}
