package xstar.com.kotlintest.apis

import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import xstar.com.kotlintest.data.GankArticle
import xstar.com.kotlintest.data.GankData
import xstar.com.kotlintest.data.IDInfo
import xstar.com.kotlintest.data.JuHeRep

/**
 * @author xstar
 * @since 5/21/17.
 */
interface ApiService {

    @GET("/idcard/index")
    fun getIdCardInfo(@Query("key") key: String, @Query("cardno") cardNo: String, @Query("dtype") dtype: String = "json"): Flowable<JuHeRep<IDInfo>>
}

interface GankApi {
    @GET("/api/data/{type}/10/{page}")
    fun gankAricles(@Path("type") type: String, @Path("page") page: Int): Flowable<GankData<GankArticle>>
}