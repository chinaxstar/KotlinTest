package xstar.com.kotlintest.apis

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import org.reactivestreams.Subscriber
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import xstar.com.kotlintest.constant.C
import xstar.com.kotlintest.data.GankArticle
import xstar.com.kotlintest.data.GankData
import xstar.com.kotlintest.data.IDInfo
import xstar.com.kotlintest.data.JuHeRep

/**
 * @author xstar
 * @since 5/21/17.
 */
class HttpMethods {

    val gson: Gson = GsonBuilder().create()
    private val api = Retrofit.Builder().baseUrl("http://apis.juhe.cn/")
            .client(OkHttpClient.Builder().addInterceptor({ it -> logNet(it) }).build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    private val gankApi = Retrofit.Builder().baseUrl("http://gank.io/")
            .client(OkHttpClient.Builder().addInterceptor({ it -> logNet(it) }).build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    val apiService = lazy { api.create(ApiService::class.java) }
    private val gankService = lazy { gankApi.create(GankApi::class.java) }


    fun logNet(chain: Interceptor.Chain): Response {
        val request = chain.request()
        logRequest(request)
        val connection = chain.connection()
        val respone = chain.proceed(request)
        val medeaType = respone.body()!!.contentType()
        val content = respone.body()!!.string()
        Log.e("content", content)
        return respone.newBuilder().body(ResponseBody.create(medeaType, content)).build()
    }

    fun logRequest(request: Request) {
        Log.e("url", request.url().toString())
        Log.e("headers", request.headers()?.toString())
    }

    fun getGankTypeArticles(type: String = "all", page: Int = 1, subscriber: Subscriber<GankData<GankArticle>>) {
        gankService.value?.gankAricles(type, page)!!.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(Consumer { subscriber.onNext(it) })
    }

    fun findIDInfo(idNum: String, subscriber: Subscriber<JuHeRep<IDInfo>>) {
        apiService.value!!.getIdCardInfo(C.JUHE_KEY, idNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber)
    }
}