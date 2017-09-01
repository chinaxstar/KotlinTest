package xstar.com.kotlintest.recycler

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * @author: xstar
 * @since: 2017-08-31.
 */

class BaseVH constructor(item: View) : RecyclerView.ViewHolder(item) {
    fun <T : View> find(resId: Int): T {
        return itemView.findViewById(resId) as T
    }
}

abstract class BaseAdapter<T> : RecyclerView.Adapter<BaseVH>() {
    var itemList: List<T>? = null
    var layout: Int? = 0
    var inflate: LayoutInflater? = null
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BaseVH {
        if (inflate == null) inflate = LayoutInflater.from(parent?.context)
        return BaseVH(inflate!!.inflate(layout!!, parent, false))
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    var onItemClickListener: OnItemClickListener? = null

    open interface OnItemClickListener {
        open fun onItemClick(adapter: RecyclerView.Adapter<BaseVH>, view: View, position: Int)
    }
}

