package xstar.com.kotlintest.util

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import org.jetbrains.anko.db.*
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType

class BasePageAdapter<T>(init: () -> Unit) : PagerAdapter() {

    init {
        init()
    }

    var itemLayout = 0
    var datas: MutableList<T>? = null
    var titleData: MutableList<String>? = null
    var bindData: ((View, Int) -> Unit)? = null

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val content = container.inflate(itemLayout, container, false)
        bindData?.invoke(content, position)
        return container
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return datas?.size ?: 0
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titleData?.get(position)
    }
}


class BaseFragmentAdapter(fm: FragmentManager, init: BaseFragmentAdapter.() -> Unit) : FragmentPagerAdapter(fm) {
    init {
        init()
    }

    var titles: MutableList<String>? = null
    var datas: MutableList<Fragment>? = null
    override fun getItem(position: Int): Fragment {
        return datas?.get(position) ?: Fragment()
    }

    override fun getCount(): Int {
        return datas?.size ?: 0
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles?.get(position)
    }
}

class SimpleRawParse<T> : MapRowParser<T> {
    override fun parseRow(columns: Map<String, Any?>): T {
        val t = clzz.newInstance()
        for (f in columnSet) {
            if (columns.containsKey(f.name)) {
                f.set(t, columns[f.name])
            }
        }
        return t
    }

    val clzz:Class<T> = (javaClass as ParameterizedType).actualTypeArguments[0] as Class<T>
    val columnSet = hashSetOf<Field>()

    init {
        columnSet.addAll(clzz.declaredFields)
    }

}