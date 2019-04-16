package xstar.com.kotlintest

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_gank_main.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import xstar.com.kotlintest.apis.HttpMethods
import xstar.com.kotlintest.constant.C
import xstar.com.kotlintest.data.GankArticle
import xstar.com.kotlintest.recycler.BaseVH
import xstar.com.kotlintest.recycler.GankAdapter
import xstar.com.kotlintest.recycler.OnItemClickListener
import xstar.com.kotlintest.util.BaseActivity
import xstar.com.kotlintest.util.BaseFragmentAdapter

/**
 * Retrofit + rxjava
 *
 * glide 图片显示
 */
class GankMainActivity : BaseActivity(R.layout.activity_gank_main) {

    var pageAdapter = BaseFragmentAdapter(supportFragmentManager) {
        datas = C.GANK_TYPES.map { s ->
            val gank = GankFragment()
            val bundle = Bundle()
            bundle.putString(C.GANK_TYPE_KEY, s)
            gank.arguments = bundle
            gank
        }.toMutableList()

        titles = C.GANK_TYPES.toMutableList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pages.adapter = pageAdapter
        pages.currentItem = 0

        val commonNavigator = CommonNavigator(this)
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                val colorTransiton = ColorTransitionPagerTitleView(context)
                colorTransiton.text = C.GANK_TYPES[index]
                colorTransiton.normalColor = Color.GRAY
                colorTransiton.selectedColor = Color.BLACK
                colorTransiton.setOnClickListener {
                    pages.currentItem = index
                }
                return colorTransiton
            }

            override fun getCount(): Int {
                return C.GANK_TYPES.size
            }

            override fun getIndicator(context: Context?): IPagerIndicator {
                return LinePagerIndicator(context)
            }

        }
        types.navigator = commonNavigator

        ViewPagerHelper.bind(types, pages)
    }


}

class GankFragment : Fragment() {
    private val compositeDisposable = CompositeDisposable()
    private var refresh_srl: SwipeRefreshLayout? = null
    private var gank_rv: RecyclerView? = null
    private var gankType: String? = null
    private var pageNum = 1
    private var dataList: MutableList<GankArticle> = arrayListOf()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        gankType = arguments?.getString(C.GANK_TYPE_KEY) ?: "all"
        val view = inflater.inflate(R.layout.gank_fragment_layout, container, false)
        refresh_srl = view.find(R.id.refresh_srl)
        gank_rv = view.find(R.id.gank_rv)
        gank_rv?.layoutManager = LinearLayoutManager(activity)
        gankAdapter.datas = dataList
        gank_rv?.adapter = gankAdapter
        refresh_srl?.setOnRefreshListener {
            pageNum = 1
            getData(gankType!!, pageNum)
        }
        gankAdapter.onItemClickListner = object : OnItemClickListener<GankArticle> {
            override fun onItemClick(holder: BaseVH, position: Int, item: GankArticle) {
                if (position == gankAdapter.itemCount - 1) {
                    pageNum++
                    getData(gankType!!, pageNum)
                } else {
                    if (C.GANK_TYPES[1] != item.type) {
                        val intent = Intent(activity, ArticleActivity::class.java)
                        intent.putExtra(C.INTENT_URL_KEY, item.url)
                        startActivity(intent)
                    } else {
                        //图片
                        val intent = Intent(activity, ImageActivity::class.java)
                        intent.putExtra(C.IMG_URL_KEY, item.url)
                        startActivity(intent)
                    }
                }
            }
        }
        return view
    }
    var first=true
    override fun onResume() {
        super.onResume()
        if(first){
            refresh_srl?.isRefreshing = true
            getData(gankType!!, pageNum)
            first=false
        }
    }

    fun getData(type: String, pageNum: Int) {
        val disp = HttpMethods.getGankTypeArticles(type = type, page = pageNum).subscribe({ t ->
            if (!t.error) {
                if (t.results.isEmpty()) {
                    activity?.toast("没有更多数据！！")
                    return@subscribe
                }
                if (refresh_srl?.isRefreshing == true) {
                    val size = dataList.size
                    dataList.clear()
                    gankAdapter.notifyItemRangeRemoved(0, size)
                    dataList.addAll(t.results)
                    gankAdapter.notifyItemRangeInserted(0, dataList.size)
                } else {
                    dataList.addAll(t.results)
                    gankAdapter.notifyItemRangeInserted(dataList.size - t.results.size, t.results.size)
                }
            } else
                activity?.toast("数据加载错误！")
            refresh_srl?.isRefreshing = false
        }, {
            activity?.toast("数据加载错误！${it.message}")
            refresh_srl?.isRefreshing = false
        })
        compositeDisposable.add(disp)
    }

    val gankAdapter = GankAdapter()

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
    }
}
