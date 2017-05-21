package xstar.com.kotlintest.apis

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import xstar.com.kotlintest.data.IDInfo
import xstar.com.kotlintest.data.Response

/**
* @author xstar
* @since 5/21/17.
*/
interface ApiService {

    @GET("/idcard/index?dtype=json&key={key}&cardno={cardNo}")
    fun getIdCardInfo(@Path("key")key:String,cardNo:String):Observable<Response<IDInfo>>
}