package xstar.com.kotlintest.apis

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import xstar.com.kotlintest.data.IDInfo
import xstar.com.kotlintest.data.Response

/**
 * @author xstar
 * @since 5/21/17.
 */
interface ApiService {

    @GET("/idcard/index")
    fun getIdCardInfo(@Query("key") key: String, @Query("cardno") cardNo: String, @Query("dtype") dtype: String = "json"): Observable<Response<IDInfo>>
}