package xstar.com.kotlintest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_gank_main.*
import xstar.com.kotlintest.apis.HttpMethods
import xstar.com.kotlintest.constant.C
import xstar.com.kotlintest.data.GankArticle
import xstar.com.kotlintest.data.GankData
import xstar.com.kotlintest.data.SimpleSubscribe
import xstar.com.kotlintest.recycler.BaseVH
import xstar.com.kotlintest.recycler.GankAdapter
import xstar.com.kotlintest.recycler.OnItemClickListener
import xstar.com.kotlintest.recycler.TypeAdapter

/**
 * Retrofit + rxjava
 *
 * glide 图片显示
 */
class GankMainActivity : AppCompatActivity() {

    val http = lazy { HttpMethods() }
    var pageNum = 1
    val dataList = ArrayList<GankArticle>()
    val typeAdapter = TypeAdapter()
    var type = C.GANK_TYPES[0]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gank_main)
        gankAdapter.datas = dataList
        gank_rv.layoutManager = LinearLayoutManager(this)
        gank_rv.adapter = gankAdapter
        refresh_srl.setOnRefreshListener {
            pageNum = 1
            getData(type, pageNum)
        }
        gankAdapter.onItemClickListner = object : OnItemClickListener<GankArticle> {
            override fun onItemClick(holder: BaseVH, position: Int, item: GankArticle) {
                if (position == gankAdapter.itemCount - 1) {
                    pageNum++
                    getData(type, pageNum)
                } else {
                    if (C.GANK_TYPES[1] != item.type) {
                        val intent = Intent(this@GankMainActivity, ArticleActivity::class.java)
                        intent.putExtra(C.INTENT_URL_KEY, item.url)
                        startActivity(intent)
                    } else {
                        //图片
                        val intent = Intent(this@GankMainActivity, ImageActivity::class.java)
                        intent.putExtra(C.IMG_URL_KEY, item.url)
                        startActivity(intent)
                    }
                }
            }
        }

        typeAdapter.datas = C.GANK_TYPES.toMutableList()
        typeAdapter.onItemClickListner = object : OnItemClickListener<String> {
            override fun onItemClick(holder: BaseVH, position: Int, item: String) {
                type = item
                pageNum = 1
                refresh_srl.isRefreshing = true
                getData(type, pageNum)
            }
        }
        types.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        types.adapter = typeAdapter
        getData(type, pageNum)
    }

    fun getData(type: String, pageNum: Int) {
        http.value.getGankTypeArticles(type = type, page = pageNum, subscriber = gankRep)
    }

    val gankAdapter = GankAdapter()

    val gankRep = object : SimpleSubscribe<GankData<GankArticle>>() {
        override fun onNext(t: GankData<GankArticle>) {
            if (!t.error) {
                if (t.results.isEmpty()) {
                    Toast.makeText(this@GankMainActivity, "没有更多数据！！", Toast.LENGTH_SHORT).show()
                    return
                }
                if (refresh_srl.isRefreshing) {
                    dataList.clear()
                    dataList.addAll(t.results)
                    gankAdapter.notifyDataSetChanged()
                } else {
                    dataList.addAll(t.results)
                    gankAdapter.notifyItemRangeInserted(dataList.size - t.results.size, t.results.size)
                }
            } else Toast.makeText(this@GankMainActivity, "数据加载错误！", Toast.LENGTH_SHORT).show()
            refresh_srl.isRefreshing = false
        }

        override fun onComplete() {
            Log.e("onComplete", "onComplete")
        }
    }
}
