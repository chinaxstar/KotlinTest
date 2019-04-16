package xstar.com.kotlintest

import android.os.Bundle
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.image_layout.*
import org.jetbrains.anko.toast
import xstar.com.kotlintest.constant.C
import xstar.com.kotlintest.util.BaseActivity

/**
 * glide 显示图片
 */
class ImageActivity : BaseActivity(R.layout.image_layout) {

    lateinit var imageUrl: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageUrl = intent.getStringExtra(C.IMG_URL_KEY)
       val loadImg= Observable.just(imageUrl).map {
        val drawable=Glide.with(this).load(imageUrl).apply(MyApp.DEFAULT_OPTIONS).submit().get()
            drawable.toBitmap()
        }.subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            image.setBitmap(it)
       }) {
           toast("加载图片失败！")
        }
        addDispose(loadImg)
    }




}
