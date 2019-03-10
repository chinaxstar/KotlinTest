package xstar.com.kotlintest.util

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import org.jetbrains.anko.wifiManager
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.io.LineNumberReader
import java.net.NetworkInterface

object AppUtil{
    fun getStatusBarHeight(context: Context): Int {
        var statusBarHeight = 0
        val res = context.resources
        val resourceId = res.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId)
        }
        return statusBarHeight
    }

    fun setStatusBarColor(act: Activity, color: Int, whiteStatusIcon: Boolean) {
        val window = act.window
        if (Build.VERSION.SDK_INT >= 23) {
            val decor = window.decorView
            var ui = decor.systemUiVisibility
            ui = when {
                whiteStatusIcon -> ui or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                else -> ui and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
            decor.systemUiVisibility = ui
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            //设置状态栏颜色
            window.statusBarColor = color
        } else if (Build.VERSION.SDK_INT >= 19) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    fun uri2RealPath(context: Context, uri: Uri): String? {
        val scheme: String? = uri.scheme ?: return uri.path
        var data: String? = null
        when (scheme) {
            ContentResolver.SCHEME_FILE -> uri.path
            ContentResolver.SCHEME_CONTENT -> {
                val cursor = context.contentResolver.query(uri, Array(1) { MediaStore.Images.ImageColumns.DATA }, null, null, null)
                cursor.moveToFirst()
                val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                if (index != -1)
                    data = cursor?.getString(index)
                cursor?.close()
            }
        }
        return data
    }

    fun getExsitFile(path: String): File? {
        val file = File(path)
        if (file.exists()) return file
        return null
    }

    fun getDeviceID(context: Context): String {
        val androidID = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val mac = getMac()
        return "$androidID$mac"
    }


    fun hasRealMacAddress(mMacAddress: String?): Boolean {
        return mMacAddress != null && "02:00:00:00:00:00" != mMacAddress
    }

    fun getWlanAddress(context: Context): String {
        val networkInterface = NetworkInterface.getNetworkInterfaces()
        while (networkInterface.hasMoreElements()) {
            val network = networkInterface.nextElement()
            if (network.name.contains("wlan0")) {
                val bytes = network.hardwareAddress
                val builder = StringBuilder()
                for (i in bytes.indices) {
                    builder.append(String.format("%02X", bytes[i]))
                }
                return builder.toString()
            }
        }
        return "unknow"
    }


    /**
     * @return wlan的物理地址
     */
    fun getMac(): String {
        var macSerial: String? = null
        var str: String? = ""
        try {
            val pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ")
            val ir = InputStreamReader(pp.inputStream)
            val input = LineNumberReader(ir)

            while (null != str) {
                str = input.readLine()
                if (str != null) {
                    macSerial = str.trim { it <= ' ' }// 去空格
                    break
                }
            }
        } catch (ex: IOException) {
            // 赋予默认值
            ex.printStackTrace()
        }

        return macSerial?.replace(":", "") ?: ""
    }

    fun getIpAddress(context: Context): String {
        val int = context.wifiManager.connectionInfo.ipAddress
        val ip1 = int.and(0xFF)
        val ip2 = int.and(0xFF00).shr(8)
        val ip3 = int.and(0xFF0000).shr(16)
        val ip4 = int.shr(24).and(0xFF)
        return "$ip1.$ip2.$ip3.$ip4"
    }
}