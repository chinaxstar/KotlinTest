package xstar.com.kotlintest

import android.database.sqlite.SQLiteDatabase
import androidx.multidex.MultiDexApplication
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import org.jetbrains.anko.db.*
import xstar.com.kotlintest.constant.C
import xstar.com.kotlintest.data.PositionInfo

/**
 * @author: xstar
 * @since: 2017-09-01.
 */
class MyApp : MultiDexApplication() {


    override fun onCreate() {
        super.onCreate()
        C.init(this)
        dbHelper.use {  }
    }

    companion object {
        val DEFAULT_OPTIONS = RequestOptions().centerCrop().priority(Priority.NORMAL).skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
    }


    val dbHelper = object : ManagedSQLiteOpenHelper(this, "base.db", null, 1) {
        override fun onCreate(db: SQLiteDatabase?) {
            db?.createTable(PositionInfo::class.java.simpleName,false,
            "latlong" to REAL,
            "latti" to REAL,
            "high" to REAL,
            "speed" to REAL,
            "time" to INTEGER
            )
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        }
    }
}