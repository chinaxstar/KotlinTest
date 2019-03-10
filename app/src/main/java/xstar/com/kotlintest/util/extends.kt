package xstar.com.kotlintest.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.Nullable
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
