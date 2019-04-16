package xstar.com.kotlintest

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.functions.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.model.PositionData
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView
import org.jetbrains.anko.find
import xstar.com.kotlintest.constant.C
import xstar.com.kotlintest.data.ModuleItem
import xstar.com.kotlintest.recycler.BaseAdapter
import xstar.com.kotlintest.recycler.BaseVH
import xstar.com.kotlintest.recycler.OnItemClickListener
import xstar.com.kotlintest.recycler.find
import xstar.com.kotlintest.util.BaseActivity
import xstar.com.kotlintest.util.BaseFragmentAdapter
import xstar.com.kotlintest.util.begPermissions
import xstar.com.kotlintest.util.dimen

class MainActivity : BaseActivity(R.layout.functions) {
    var titles = mutableListOf("首页", "工具箱", "关于")
    var pageAdapter = BaseFragmentAdapter(supportFragmentManager) {
        datas = mutableListOf(GankFragment(), ToolBoxFragment(), AboutFragment())
    }

    var savePermission = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionResult = { s, b ->
            savePermission = b
        }

        begPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, C.PERMISSION_REQUEST_CODE,permissionResult!!)

        val commonNavigator = CommonNavigator(this)
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                val colorTransiton = ColorTransitionPagerTitleView(context)
                colorTransiton.normalColor = Color.BLACK
                colorTransiton.selectedColor = Color.BLUE
                colorTransiton.text = titles[index]
                colorTransiton.setOnClickListener {
                    mainPages.currentItem = index
                }
                return colorTransiton
            }

            override fun getCount(): Int {
                return titles.size
            }

            override fun getIndicator(context: Context?): IPagerIndicator {
                return object : IPagerIndicator {
                    override fun onPositionDataProvide(dataList: MutableList<PositionData>?) {

                    }

                    override fun onPageScrollStateChanged(state: Int) {

                    }

                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                    }

                    override fun onPageSelected(position: Int) {

                    }
                }
            }

        }

        mainPages.adapter = pageAdapter
        mainTabs.navigator = commonNavigator
        ViewPagerHelper.bind(mainTabs, mainPages)
    }
}

class ToolBoxFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.main_fragment_layout, container, false)
        val functions = view.find<RecyclerView>(R.id.functions)
        functions.layoutManager = GridLayoutManager(activity, 4)
        functions.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.set(dimen(R.dimen.dp_5), dimen(R.dimen.dp_5), dimen(R.dimen.dp_5), dimen(R.dimen.dp_5))
            }
        })
        val adapter = BaseAdapter<ModuleItem> {
            itemLayout = R.layout.functions_item
            bindData = { h, p ->
                val item = datas!![p]
                h.find<TextView>(R.id.title).text = item.functionName
                h.itemView.setOnClickListener { onItemClickListner?.onItemClick(h, p, item) }
            }
        }
        adapter.datas = arrayListOf(ModuleItem("GANK集中营", GankMainActivity::class.java, null)
                , ModuleItem("蓝牙", BluetoothActivity::class.java, null)
                , ModuleItem("纪念日", ImportantDayActivity::class.java, null)
                , ModuleItem("NFC", NFCActivity::class.java, null)
                , ModuleItem("图片", PhotoActivity::class.java, null)
                , ModuleItem("扫码", ScannerActivity::class.java, null)
        )
        adapter.onItemClickListner = object : OnItemClickListener<ModuleItem> {
            override fun onItemClick(holder: BaseVH, position: Int, item: ModuleItem) {
                val intent = Intent(activity, item.turnTo)
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
            }
        }

        functions.adapter = adapter
        return view
    }
}

class AboutFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.about_fragment_alyout, container, false)
        val content = view.find<EditText>(R.id.content)
        content.isEnabled = false
        content.append("作者：@无名 \n")
        content.append("Github: https://github.com/chinaxstar \n")
        content.append("E-mail: xyx_xstar@sina.cn \n")
        return view
    }
}