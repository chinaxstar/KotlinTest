package xstar.com.kotlintest

import android.app.Application
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions

/**
 * @author: xstar
 * @since: 2017-09-01.
 */
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        val DEFAULT_OPTIONS = RequestOptions().centerCrop().priority(Priority.NORMAL).skipMemoryCache(false)
    }
}