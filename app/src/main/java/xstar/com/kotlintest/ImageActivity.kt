package xstar.com.kotlintest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.image_layout.*
import xstar.com.kotlintest.constant.C

/**
 * glide 显示图片
 */
class ImageActivity : AppCompatActivity() {

    lateinit var imageUrl: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_layout)
        imageUrl = intent.getStringExtra(C.IMG_URL_KEY)
        Glide.with(this).load(imageUrl).apply(MyApp.DEFAULT_OPTIONS).into(image)
    }




}
