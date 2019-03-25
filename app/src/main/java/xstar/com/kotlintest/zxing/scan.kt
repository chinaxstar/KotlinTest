package xstar.com.kotlintest.zxing

import android.graphics.Bitmap
import android.os.Handler
import com.google.zxing.Result
import xstar.com.kotlintest.zxing.view.ViewfinderView

interface ScanInterface {

    fun getViewfinderView(): ViewfinderView
    fun getHandler(): Handler?
    fun drawViewfinder()
    fun handleDecode(result: Result, barcode: Bitmap)
//    fun setResult(code:Int, intent: Intent)
}