package xstar.com.kotlintest.apis

import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author xstar
 * @since 5/21/17.
 */
class HttpMethods {

    open val gson: Gson =Gson()

    val api:Retrofit= Retrofit.Builder().baseUrl("http://apis.juhe.cn/").client(OkHttpClient())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson)).build()
    open val apiService=api.create(ApiService::class.java)


}