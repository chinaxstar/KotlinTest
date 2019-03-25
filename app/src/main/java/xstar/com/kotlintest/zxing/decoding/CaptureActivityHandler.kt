/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xstar.com.kotlintest.zxing.decoding

import android.graphics.Bitmap
import android.os.Handler
import android.os.Message
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import xstar.com.kotlintest.R
import xstar.com.kotlintest.zxing.ScanInterface
import xstar.com.kotlintest.zxing.camera.CameraManager
import xstar.com.kotlintest.zxing.view.ViewfinderResultPointCallback
import java.util.*

/**
 * This class handles all the messaging which comprises the state machine for capture.
 */
class CaptureActivityHandler(act: ScanInterface, decodeFormats: Vector<BarcodeFormat>?,
                             characterSet: String?) : Handler() {
    val activity = act
    private val decodeThread: DecodeThread
    private var state: State? = null

    private enum class State {
        PREVIEW,
        SUCCESS,
        DONE
    }

    init {
        decodeThread = DecodeThread(activity, decodeFormats, characterSet,
                ViewfinderResultPointCallback(activity.getViewfinderView()))
        decodeThread.start()
        state = State.SUCCESS
        // Start ourselves capturing previews and decoding.
        CameraManager.get().startPreview()
        restartPreviewAndDecode()
    }

    override fun handleMessage(message: Message) {
        when (message.what) {
            R.id.auto_focus ->
                //Log.d(TAG, "Got auto-focus message");
                // When one auto focus pass finishes, start another. This is the closest thing to
                // continuous AF. It does seem to hunt a bit, but I'm not sure what else to do.
                if (state == State.PREVIEW) {
                    CameraManager.get().requestAutoFocus(this, R.id.auto_focus)
                }
            R.id.restart_preview -> {
                Log.d(TAG, "Got restart preview message")
                restartPreviewAndDecode()
            }
            R.id.decode_succeeded -> {
                Log.d(TAG, "Got decode succeeded message")
                state = State.SUCCESS
                val bundle = message.data

                /** */
                val barcode = bundle?.getParcelable<Bitmap>(DecodeThread.BARCODE_BITMAP)//閿熸枻鎷烽敓鐭唻鎷烽敓鏂ゆ嫹閿熺绛规嫹

                activity.handleDecode(message.obj as Result, barcode!!)//閿熸枻鎷烽敓鎴枻鎷烽敓?        /***********************************************************************/
            }
            R.id.decode_failed -> {
                // We're decoding as fast as possible, so when one decode fails, start another.
                state = State.PREVIEW
                CameraManager.get().requestPreviewFrame(decodeThread.getDecodeHandler(), R.id.decode)
            }
//            R.id.return_scan_result -> {
//                Log.d(TAG, "Got return scan result message")
//                activity.setResult(Activity.RESULT_OK, message.obj as Intent)
//                activity.finish()
//            }
//            R.id.launch_product_query -> {
//                Log.d(TAG, "Got product query message")
//                val url = message.obj as String
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
//                activity.startActivity(intent)
//            }
        }
    }

    fun quitSynchronously() {
        state = State.DONE
        CameraManager.get().stopPreview()
        val quit = Message.obtain(decodeThread.handler, R.id.quit)
        quit.sendToTarget()
        try {
            decodeThread.join()
        } catch (e: InterruptedException) {
            // continue
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(R.id.decode_succeeded)
        removeMessages(R.id.decode_failed)
    }

    private fun restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW
            CameraManager.get().requestPreviewFrame(decodeThread.getDecodeHandler(), R.id.decode)
            CameraManager.get().requestAutoFocus(this, R.id.auto_focus)
            activity.drawViewfinder()
        }
    }

    companion object {

        private val TAG = CaptureActivityHandler::class.java.simpleName
    }

}
