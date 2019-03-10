package xstar.com.kotlintest.data

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