package xstar.com.kotlintest.apis

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author xstar
 * @since 5/21/17.
 */
class HttpMethods {

    val gson: Gson = GsonBuilder().create()
    val api: Retrofit = Retrofit.Builder().baseUrl("http://apis.juhe.cn/")
            .client(OkHttpClient.Builder().addInterceptor({ it -> logNet(it) }).build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    val apiService = api.create(ApiService::class.java)

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
}