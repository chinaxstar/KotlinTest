package xstar.com.kotlintest

import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.image_layout.*
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
        Glide.with(this).load(imageUrl).apply(MyApp.DEFAULT_OPTIONS).into(image)
    }




}
