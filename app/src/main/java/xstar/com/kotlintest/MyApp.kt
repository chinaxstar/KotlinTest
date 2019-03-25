package xstar.com.kotlintest

import androidx.multidex.MultiDexApplication
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import xstar.com.kotlintest.constant.C

/**
 * @author: xstar
 * @since: 2017-09-01.
 */
class MyApp : MultiDexApplication() {


    override fun onCreate() {
        super.onCreate()
        C.init(this)
    }

    companion object {
        val DEFAULT_OPTIONS = RequestOptions().centerCrop().priority(Priority.NORMAL).skipMemoryCache(false)
    }
}