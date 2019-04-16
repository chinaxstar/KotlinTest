package xstar.com.kotlintest

import android.Manifest
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.view.SurfaceHolder
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_scan.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.toast
import xstar.com.kotlintest.constant.C
import xstar.com.kotlintest.util.BaseActivity
import xstar.com.kotlintest.util.begPermissions
import xstar.com.kotlintest.zxing.ScanInterface
import xstar.com.kotlintest.zxing.camera.CameraManager
import xstar.com.kotlintest.zxing.decoding.CaptureActivityHandler
import xstar.com.kotlintest.zxing.decoding.InactivityTimer
import xstar.com.kotlintest.zxing.view.ViewfinderView
import java.io.IOException
import java.util.*

/**
 * 扫码页
 * @author: xstar
 * @since: 2018-05-18.
 */

class ScannerActivity : BaseActivity(R.layout.activity_scan), SurfaceHolder.Callback, ScanInterface {
    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        hasSurface = false
        holder?.removeCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        if (!hasSurface) {
            hasSurface = true
            initCamera(holder)
        }
        holder?.addCallback(this)
    }

    override fun getViewfinderView(): ViewfinderView {
        return floating_layer
    }

    override fun getHandler(): Handler? {
        return handler
    }

    override fun drawViewfinder() {
        finder?.drawViewfinder()
    }


    private lateinit var inactivityTimer: InactivityTimer
    var finder: ViewfinderView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CameraManager.init(this)
        inactivityTimer = InactivityTimer(this)
        permissionResult = { s, b ->
            if (!b) finish() else {
                initCamera(scanner_screen.holder)
                scanner_screen.holder.addCallback(this@ScannerActivity)
                floating_layer.drawViewfinder()
            }
        }
        begPermissions(Manifest.permission.CAMERA, requestCode = C.CAMERA_REQUEST_CODE, result = permissionResult!!)
    }


    private var hasSurface = false
    override fun onResume() {
        super.onResume()
        val surfaceHolder = scanner_screen?.holder
        if (hasSurface) {
            initCamera(surfaceHolder)
        }
        surfaceHolder?.addCallback(this)
    }

    private var handler: CaptureActivityHandler? = null
    private var decodeFormats: Vector<BarcodeFormat>? = null
    private var characterSet: String? = null

    private fun initCamera(surfaceHolder: SurfaceHolder?) {
        try {
            CameraManager.get().openDriver(surfaceHolder)
        } catch (ioe: IOException) {
            ioe.printStackTrace()
            return
        } catch (e: RuntimeException) {
            e.printStackTrace()
            return
        }

        handler = CaptureActivityHandler(this, decodeFormats, characterSet)
    }

    override fun onPause() {
        super.onPause()
        handler?.quitSynchronously()
        CameraManager.get()?.closeDriver()
    }

    override fun handleDecode(result: Result, barcode: Bitmap) {
        inactivityTimer.onActivity()
        val resultString = result.text
        if (resultString.isNullOrEmpty()) {
            toast("扫码失败！")
        } else {
            alert {
                message = resultString
                okButton {
                    it.dismiss()
                    handler?.obtainMessage(R.id.decode_failed)?.sendToTarget()
                }
            }.show()
        }
    }
}

