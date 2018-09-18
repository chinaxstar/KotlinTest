package xstar.com.kotlintest.recycler

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import xstar.com.kotlintest.MyApp
import xstar.com.kotlintest.R
import xstar.com.kotlintest.constant.C
import xstar.com.kotlintest.data.GankArticle

/**
 * @author: xstar
 * @since: 2017-08-31.
 */

class BaseVH constructor(item: View) : RecyclerView.ViewHolder(item) {
    fun <T : View> find(resId: Int): T {
        return itemView.findViewById(resId) as T
    }
}

open abstract class BaseAdapter<T> : RecyclerView.Adapter<BaseVH>() {
    var itemList: List<T>? = null
    var layout: Int? = 0
    var footLayout: Int? = 0
    var inflate: LayoutInflater? = null
    var hasFooter = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseVH {
        if (inflate == null) inflate = LayoutInflater.from(parent?.context)
        return BaseVH(inflate!!.inflate(viewType, parent, false))
    }

    override fun onBindViewHolder(holder: BaseVH, position: Int) {
        if (hasFooter && position == itemCount - 1) {
            holder?.itemView?.setOnClickListener { onFootClickListener?.onItemClick(this, it, position) }
            onFootBind(holder, position)
        } else {
            holder?.itemView?.setOnClickListener { onItemClickListener?.onItemClick(this, it, position) }
            onBindView(holder, position)
        }
    }

    open fun onFootBind(holder: BaseVH?, position: Int) {}
    abstract fun onBindView(holder: BaseVH?, position: Int)

    override fun getItemCount(): Int {
        val size = itemList?.size ?: 0
        if (hasFooter) size.plus(1)
        return size
    }

    var onItemClickListener: OnItemClickListener? = null
    var onFootClickListener: OnItemClickListener? = null

    open interface OnItemClickListener {
        open fun onItemClick(adapter: RecyclerView.Adapter<BaseVH>, view: View, position: Int)
    }

    override fun getItemViewType(position: Int): Int {
        var lay = layout
        if (hasFooter && footLayout != 0 && position == itemCount - 1) lay = footLayout
        return lay!!
    }

}

class GankAdapter : BaseAdapter<GankArticle>() {
    val options = MyApp.DEFAULT_OPTIONS
    val tranOption = DrawableTransitionOptions().crossFade(450)!!
    override fun onBindView(holder: BaseVH?, position: Int) {
        holder?.itemView?.setOnClickListener { onItemClickListener?.onItemClick(this, it, position) }
        val item = itemList?.get(position)
        holder?.find<TextView>(R.id.article_title)?.text = item?.desc
        holder?.find<TextView>(R.id.author_name)?.text = item?.who
        holder?.find<TextView>(R.id.publish_date)?.text = item?.publishedAt?.split("T")?.get(0)
        val image = holder?.find<ImageView>(R.id.article_image)
        if (C.GANK_TYPES[1].equals(item?.type)) {
            //图片
            image?.visibility = View.VISIBLE
            Glide.with(holder?.itemView?.context).load(item?.url).transition(tranOption).apply(options).into(image)
        } else if (item?.images != null && item.images.isNotEmpty()) {
            image?.visibility = View.VISIBLE
            Glide.with(holder?.itemView?.context).load(item.images[0]).transition(tranOption).apply(options).into(image)
        } else image?.visibility = View.GONE
    }

    init {
        layout = R.layout.item_gank_article
        footLayout = R.layout.item_footer_layout
        hasFooter = true
    }
}

class TypeAdapter : BaseAdapter<String>() {
    init {
        layout = R.layout.item_type_layout
    }

    override fun onBindView(holder: BaseVH?, position: Int) {
        holder?.find<TextView>(R.id.type_name)?.text = itemList?.get(position)
    }
}

