package xstar.com.kotlintest

import android.graphics.Rect
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.DisplayMetrics
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_gank_main.*
import xstar.com.kotlintest.apis.HttpMethods
import xstar.com.kotlintest.data.GankArticle
import xstar.com.kotlintest.data.GankData
import xstar.com.kotlintest.data.SimpleSubscribe
import xstar.com.kotlintest.recycler.BaseAdapter
import xstar.com.kotlintest.recycler.BaseVH

class GankMainActivity : AppCompatActivity() {

    val http = lazy { HttpMethods() }
    var size: DisplayMetrics? = null

    var options: RequestOptions? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gank_main)
        gankAdapter.itemList = ArrayList<GankArticle>()
        gank_rv.layoutManager = LinearLayoutManager(this)
        gank_rv.adapter = gankAdapter
        http.value!!.getGankTypeArticles(subscriber = gankRep)
        val rect = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(rect)
        options = RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher).priority(Priority.NORMAL).diskCacheStrategy(DiskCacheStrategy.NONE).override(rect.widthPixels, (rect.widthPixels * 9 / 16f).toInt())
    }


    val gankAdapter = object : BaseAdapter<GankArticle>() {
        override fun onBindViewHolder(holder: BaseVH?, position: Int) {
            val item = itemList!!.get(position)
            holder?.find<TextView>(R.id.article_title)?.setText(item.desc)
            holder?.find<TextView>(R.id.author_name)?.setText(item.who)
            holder?.find<TextView>(R.id.publish_date)?.setText(item.publishedAt.split("T")[0])
            val image = holder?.find<ImageView>(R.id.article_image)
            if (item.images != null && item.images.isNotEmpty())
                Glide.with(this@GankMainActivity).load(item.images[0]).apply(options!!).into(image)
            else Glide.with(this@GankMainActivity).load(R.mipmap.ic_launcher).apply(options!!).into(image)
        }

        init {
            layout = R.layout.item_gank_article
        }
    }

    val gankRep = object : SimpleSubscribe<GankData<GankArticle>>() {
        override fun onNext(t: GankData<GankArticle>) {
            if (!t.error) {
                gankAdapter.itemList = t.results
                gankAdapter.notifyDataSetChanged()
            } else Toast.makeText(this@GankMainActivity, "数据加载错误！", Toast.LENGTH_SHORT).show()
        }
    }
}
