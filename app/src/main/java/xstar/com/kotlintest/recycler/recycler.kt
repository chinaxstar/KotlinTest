package xstar.com.kotlintest.recycler

import android.util.SparseBooleanArray
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import org.jetbrains.anko.find
import xstar.com.kotlintest.MyApp
import xstar.com.kotlintest.R
import xstar.com.kotlintest.constant.C
import xstar.com.kotlintest.data.GankArticle
import xstar.com.kotlintest.util.inflate

/**
 * @author: xstar
 * @since: 2017-08-31.
 */

inline fun <reified T : View> androidx.recyclerview.widget.RecyclerView.ViewHolder.find(@IdRes id: Int): T {
    return itemView.find(id)
}

open class BaseVH(view: () -> View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view())

interface OnItemClickListener<M> {
    fun onItemClick(holder: BaseVH, position: Int, item: M)
}

interface OnItemLongClickListener<M> {
    fun onItemLongClick(holder: BaseVH, position: Int, item: M)
}

interface OnItemViewsClickListener<M> {
    fun onItemViewClick(holder: BaseVH, view: View, position: Int, item: M)
}

open class BaseAdapter<M>(init: BaseAdapter<M>.() -> Unit) : androidx.recyclerview.widget.RecyclerView.Adapter<BaseVH>() {
    var tag: Any? = null
    var bindData: ((BaseVH, Int) -> Unit)? = null
    var viewType: ((Int) -> Int)? = null
    var datas: MutableList<M>? = null
    var onItemClickListner: OnItemClickListener<M>? = null
    var onItemLongClickListner: OnItemLongClickListener<M>? = null
    private val onItemViewClickListeners = HashMap<Int, OnItemViewsClickListener<M>>()
    var selects = SparseBooleanArray()
    var itemLayout = 0

    init {
        init()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseVH {
        return BaseVH { parent.inflate(viewType, parent, false) }
    }

    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onBindViewHolder(holder: BaseVH, position: Int) {
        bindData?.invoke(holder, position)
    }

    fun addOnItemViewListener(@IdRes id: Int, onItemViewsClickListener: OnItemViewsClickListener<M>) {
        onItemViewClickListeners[id] = onItemViewsClickListener
    }

    fun findOnItemViewListener(@IdRes id: Int): OnItemViewsClickListener<M>? {
        return onItemViewClickListeners[id]
    }

    override fun getItemViewType(position: Int): Int {
        return viewType?.invoke(position) ?: itemLayout
    }
}

open class HeaderFooterAdapter<M>(logic: HeaderFooterAdapter<M>.() -> Unit = {}) : BaseAdapter<M>({}) {
    var footerLayout = 0
    var headerLayout = 0

    init {
        logic()
        viewType = { p ->
            if (p == 0 && headerLayout != 0) headerLayout
            else if (p == itemCount - 1 && footerLayout != 0) footerLayout
            else itemLayout
        }
    }

    override fun getItemCount(): Int {
        var size = datas?.size ?: 0
        if (headerLayout != 0) size += 1
        if (footerLayout != 0) size += 1
        return size
    }
}

class GankAdapter : HeaderFooterAdapter<GankArticle>({
    itemLayout = R.layout.item_gank_article
    footerLayout = R.layout.item_footer_layout
    val options = MyApp.DEFAULT_OPTIONS
    val tranOption = DrawableTransitionOptions().crossFade(450)
    bindData = { h, p ->

        if (footerLayout != 0 && p == itemCount - 1) {
            h.itemView.setOnClickListener {
                onItemClickListner?.onItemClick(h, p,
                        GankArticle("", "", "", "", "", "", "", false, "", ArrayList()))
            }
        } else if (headerLayout != 0 && p == 0) {
            h.itemView.setOnClickListener {
                onItemClickListner?.onItemClick(h, p,
                        GankArticle("", "", "", "", "", "", "", false, "", ArrayList()))
            }
        } else {
            val item = datas!![p]
            h.itemView.setOnClickListener { onItemClickListner?.onItemClick(h, p, item) }
            h.find<TextView>(R.id.article_title).text = item.desc
            h.find<TextView>(R.id.author_name).text = item.who
            h.find<TextView>(R.id.publish_date).text = item.publishedAt.split("T").get(0)
            val image = h.find<ImageView>(R.id.article_image)
            if (C.GANK_TYPES[1] == item.type) {
                //图片
                image.visibility = View.VISIBLE
                Glide.with(h.itemView.context).load(item.url).transition(tranOption).apply(options).into(image)
            } else if (item.images?.isNotEmpty() == true) {
                image.visibility = View.VISIBLE
                Glide.with(h.itemView.context).load(item.images[0]).transition(tranOption).apply(options).into(image)
            } else image.visibility = View.GONE
        }


    }
}) {
    override fun getItemCount(): Int {
        val size = datas?.size ?: 0
        return when (size) {
            0 -> 0
            else -> if (itemLayout != 0) size + 1
            else 0
        }
    }
}

class TypeAdapter : BaseAdapter<String>({
    itemLayout = R.layout.item_type_layout
    bindData = { h, p ->
        h.find<TextView>(R.id.type_name).text = datas!![p]
        h.itemView.setOnClickListener { onItemClickListner?.onItemClick(h, p, datas!![p]) }
    }
})

