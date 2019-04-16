package xstar.com.kotlintest.data

import android.app.Activity
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

/**
 * @author xstar
 * @since 5/21/17.
 */

data class JuHeRep<out T>(val error_code: String,
                          val reason: String,
                          val resultcode: String,
                          val result: T)

data class IDInfo(val area: String, val sex: String, val birthday: String, val verify: String)

//gank.io 类型文章
data class GankData<out T>(val error: Boolean, val results: List<T>)

data class GankArticle(val _id: String, val createdAt: String, val desc: String, val publishedAt: String, val source: String, val type: String, val url: String, val used: Boolean, val who: String, val images: List<String>?)

open class SimpleSubscribe<T> : Subscriber<T> {
    override fun onComplete() {
    }

    override fun onNext(t: T) {
    }

    override fun onError(t: Throwable?) {
    }

    override fun onSubscribe(s: Subscription?) {
    }

}

data class ModuleItem(val functionName: String, val turnTo: Class<*>, val iconId: Int?)

data class PhotoTrans(val transName: String, val transCode: Int)

/**
 * 权限事件
 */
data class PermissionEvent(val permission: String)

data class MsgEvent(val code: Int)

/**
 * 位置信息
 */
data class PositionInfo(val latlong:Double,val latti:Double,val high:Double,val speed:Double,val time:Long)

/**
 * 纪念日
 */
data class ImportantDay(val time:Long,val description:String)