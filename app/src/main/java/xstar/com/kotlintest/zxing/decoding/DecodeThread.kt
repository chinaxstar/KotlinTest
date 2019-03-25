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

import android.os.Handler
import android.os.Looper
import com.google.zxing.BarcodeFormat
import com.google.zxing.DecodeHintType
import com.google.zxing.ResultPointCallback
import xstar.com.kotlintest.zxing.ScanInterface
import java.util.*
import java.util.concurrent.CountDownLatch

/**
 * This thread does all the heavy lifting of decoding the images.
 * 解码线程
 */
internal class DecodeThread(private val activity: ScanInterface,
                            decodeFormats: Vector<BarcodeFormat>?,
                            characterSet: String?,
                            resultPointCallback: ResultPointCallback) : Thread() {
    private val hints: Hashtable<DecodeHintType, Any>
    var handler: Handler? = null
    private val handlerInitLatch: CountDownLatch

    init {
        var decodeFormats = decodeFormats
        handlerInitLatch = CountDownLatch(1)

        hints = Hashtable(3)

        if (decodeFormats == null || decodeFormats.isEmpty()) {
            decodeFormats = Vector()
//            decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS)
//            decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS)
//            decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS)
            decodeFormats.addAll(BarcodeFormat.values().toList())
        }

        hints[DecodeHintType.POSSIBLE_FORMATS] = decodeFormats

        if (characterSet != null) {
            hints[DecodeHintType.CHARACTER_SET] = characterSet
        }

        hints[DecodeHintType.NEED_RESULT_POINT_CALLBACK] = resultPointCallback
    }

    fun getDecodeHandler(): Handler? {
        try {
            handlerInitLatch.await()
        } catch (ie: InterruptedException) {
            // continue?
        }

        return handler
    }

    override fun run() {
        Looper.prepare()
        handler = DecodeHandler(activity, hints)
        handlerInitLatch.countDown()
        Looper.loop()
    }

    companion object {

        const val BARCODE_BITMAP = "barcode_bitmap"
    }

}
