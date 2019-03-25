/*
 * Copyright (C) 2010 ZXing authors
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

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import xstar.com.kotlintest.R
import xstar.com.kotlintest.zxing.ScanInterface
import xstar.com.kotlintest.zxing.camera.CameraManager
import java.util.*

internal class DecodeHandler(private val activity: ScanInterface, hints: Hashtable<DecodeHintType, Any>) : Handler() {
    private val multiFormatReader: MultiFormatReader = MultiFormatReader()

    init {
        multiFormatReader.setHints(hints)
    }

    override fun handleMessage(message: Message) {
        when (message.what) {
            R.id.decode ->
                //Log.d(TAG, "Got decode message");
                decode(message.obj as ByteArray, message.arg1, message.arg2)
            R.id.quit -> Looper.myLooper()!!.quit()
        }
    }

    /**
     * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
     * reuse the same reader objects from one decode to the next.
     *
     * @param data   The YUV preview frame.
     * @param width  The width of the preview frame.
     * @param height The height of the preview frame.
     */
    private fun decode(data: ByteArray, width: Int, height: Int) {
        var width = width
        var height = height
        val start = System.currentTimeMillis()
        var rawResult: Result? = null

        //modify here
        val rotatedData = ByteArray(data.size)
        for (y in 0 until height) {
            for (x in 0 until width)
                rotatedData[x * height + height - y - 1] = data[x + y * width]
        }
        val tmp = width // Here we are swapping, that's the difference to #11
        width = height
        height = tmp

        val source = CameraManager.get().buildLuminanceSource(rotatedData, width, height)
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        try {
            rawResult = multiFormatReader.decodeWithState(bitmap)
        } catch (re: ReaderException) {
            // continue
        } finally {
            multiFormatReader.reset()
        }

        if (rawResult != null) {
            val end = System.currentTimeMillis()
            Log.d(TAG, "Found barcode (" + (end - start) + " ms):\n" + rawResult.toString())
            val message = Message.obtain(activity.getHandler(), R.id.decode_succeeded, rawResult)
            val bundle = Bundle()
            bundle.putParcelable(DecodeThread.BARCODE_BITMAP, source.renderCroppedGreyscaleBitmap())
            message.data = bundle
            //Log.d(TAG, "Sending decode succeeded message...");
            message.sendToTarget()
        } else {
            val message = Message.obtain(activity.getHandler(), R.id.decode_failed)
            message.sendToTarget()
        }
    }

    companion object {

        private val TAG = DecodeHandler::class.java.simpleName
    }

}
