package xstar.com.kotlintest.constant

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import org.jetbrains.anko.windowManager

/**
 * @author xstar
 * @since 5/21/17.
 */
object C {
    val JUHE_KEY = "dc13c2888a4c86736c1a8aa41ffa8f31"
    val ID_CARD = 0x0
    val QQ_NUMBER = 0x1
    val QQ_GROUP = 0x2
    val TEL_NUMBER = 0x3
    val PHOME_NUMBER = 0x4

    val INTENT_URL_KEY = "intent.url.key"
    val GANK_TYPES = arrayOf("all", "福利", "Android", "iOS", "休息视频", "拓展资源", "前端", "瞎推荐", "App")

    val IMG_URL_KEY = "gank_img_path"
    val GANK_TYPE_KEY = "gank_type_key"
    val PERMISSION_REQUEST_CODE = 0x101
    val CAMERA_REQUEST_CODE = 0x102
    val NFC_ENABLE_CODE = 0X103
    val BLUETOOTH_ENABLE_CODE=0x104

    fun init(context: Context) {
        val win = context.windowManager
        val dm = DisplayMetrics()
        win.defaultDisplay.getMetrics(dm)
        SCREEN_W = dm.widthPixels
        SCREEN_H = dm.heightPixels
        DENSITY = dm.density
        SCALE_DENSITY = dm.scaledDensity

    }

    var SCREEN_W = 0
    var SCREEN_H = 0
    var DENSITY = 0f
    var SCALE_DENSITY = 0f

    val PHOTO_TRANS_NONE = 0//无变换
    val PHOTO_TRANS_GRAY = 1//灰度
    val PHOTO_TRANS_SKETCH = 2//素描
    val PHOTO_TRANS_PENCIL = 3//铅笔画
    val PHOTO_TRANS_CHARS = 5//字符图


    val IMAGE_CHARS = "\$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\\\"^`'. "
}