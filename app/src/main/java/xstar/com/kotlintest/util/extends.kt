package xstar.com.kotlintest.util

import android.app.Activity
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DimenRes
import androidx.annotation.IntegerRes
import androidx.annotation.LayoutRes
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.dimen
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*


fun Context.getVersionName(): String? {
    return packageManager.getPackageInfo(packageName, 0)?.versionName
}

fun Context.getVersionCode(): Int {
    return packageManager.getPackageInfo(packageName, 0)?.versionCode ?: 0
}

fun Fragment.dimen(@DimenRes id: Int): Int {
    return activity?.dimen(id) ?: 0
}

fun View.inflate(@LayoutRes id: Int, @Nullable root: ViewGroup? = null, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(id, root, attachToRoot)
}

fun Context.inflate(@LayoutRes id: Int, @Nullable root: ViewGroup? = null, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(this).inflate(id, root, attachToRoot)
}

fun Date.format(pattern: String = "yyyy/MM/dd HH:mm:ss:SSS"): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}

fun Date.timeBefore(date: Date): Boolean {
    return timeOfDay() < date.timeOfDay()
}

fun Date.timeAfter(date: Date): Boolean {
    return timeOfDay() > date.timeOfDay()
}

fun Date.timeOfDay(): Long {
    val thisCal = Calendar.getInstance()
    thisCal.time = this
    return thisCal.get(Calendar.HOUR_OF_DAY).times(60 * 60 * 1000L)
            .plus(thisCal.get(Calendar.MINUTE).times(60 * 1000))
            .plus(thisCal.get(Calendar.SECOND).times(1000))
            .plus(thisCal.get(Calendar.MILLISECOND))
}

fun String.parseDate(pattern: String = "yyyy-MM-dd HH:mm:ss"): Date {
    val format = SimpleDateFormat(pattern)
    return format.parse(this)
}

fun Double.toMoneyStr(): String {
    return String.format("%.2f", toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble())
}

fun BigDecimal.toMoneyStr(): String {
    return setScale(2, RoundingMode.HALF_UP).toString()
}

fun String.format(prefix: Char = '?', vararg args: String): String {
    val sb = StringBuilder()
    var i = 0
    val len = args.size
    for (c in this.indices) {
        if (this[c] == prefix && i < len) {
            sb.append(args[i])
            i++
        } else
            sb.append(this[c])
    }
    return sb.toString()
}

fun BluetoothClass.majorDeviceClassString(): String {
    return when (majorDeviceClass) {
        BluetoothClass.Device.Major.COMPUTER -> "电脑"
        BluetoothClass.Device.Major.HEALTH -> "健康"
        BluetoothClass.Device.Major.AUDIO_VIDEO -> "音频/视频"
        BluetoothClass.Device.Major.IMAGING -> "成像设备"
        BluetoothClass.Device.Major.MISC -> "杂项"
        BluetoothClass.Device.Major.NETWORKING -> "网络设备"
        BluetoothClass.Device.Major.PERIPHERAL -> "外设"
        BluetoothClass.Device.Major.PHONE -> "手机"
        BluetoothClass.Device.Major.TOY -> "玩具"
        BluetoothClass.Device.Major.UNCATEGORIZED -> "未分类"
        BluetoothClass.Device.Major.WEARABLE -> "穿戴设备"
        else -> "未知设备"
    }
}

fun Activity.begPermissions(permission: String, requestCode: Int, result: (String, Boolean) -> Unit = { _, _ -> }) {
    if (Build.VERSION.SDK_INT >= 23) {
        if (checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(permission), requestCode)
            return
        }
    }
    result(permission, true)
}


inline fun <reified R> Flowable<R>.composeUIThread(): Flowable<R> {
    return compose {
        it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}

inline fun <reified R> Flowable<R>.composeUIFromNewThread(): Flowable<R> {
    return compose {
        it.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
    }
}