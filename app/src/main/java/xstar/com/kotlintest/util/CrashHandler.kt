package xstar.com.kotlintest.util

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Build
import android.os.Environment
import android.os.Looper
import android.util.Log
import android.widget.Toast
import java.io.*
import java.util.*

/**
 *
 *
 * 崩溃处理程序
 *
 *
 *保证只有一个CrashHandler实例
 * Created by xstar on 2015/5/12.
 */
class CrashHandler private constructor() : Thread.UncaughtExceptionHandler {
    /**
     * 程序的Context对象
     */
    private var mContext: Context? = null
    /**
     * 系统默认的UncaughtException处理类
     */
    private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null

    /**
     * 使用Properties来保存设备的信息和错误堆栈信息
     */
    private val mDeviceCrashInfo = Properties()

    var isSave = true

    private var path: String? = null

    val crashDir: File
        get() {
            path = Environment.getExternalStorageDirectory().path
            path = "$path/${mContext?.packageName ?: ""}/ExceptionInfo"
            return File(path!!)
        }

    var onCrashListener: OnCrashListener? = object : OnCrashListener {
        override fun onCrash(context: Context?, thread: Thread, ex: Throwable) {
            object : Thread() {
                override fun run() {
                    Looper.prepare()
                    Toast.makeText(mContext, "系统故障！", Toast.LENGTH_LONG).show()
                    Looper.loop()
                }
            }.start()
            if (isSave) {
                saveCrashInfoToFile(ex)
            }
            try {
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            System.exit(0)
        }
    }

    /**
     * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
     *
     * @param context
     */
    fun init(context: Context) {
        mContext = context
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
        val file = crashDir
        if (!file.exists()) {
            file.mkdirs()
        } else {
            //清空一周前的日志
            delLogFileWeekAgo(file)
        }
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    override fun uncaughtException(thread: Thread, ex: Throwable) {
        if (onCrashListener != null) {
            onCrashListener!!.onCrash(mContext, thread, ex)
        }
    }

    /**
     * 收集程序崩溃的设备信息
     *
     * @param ctx
     */
    fun collectCrashDeviceInfo(ctx: Context) {
        try {
            // Class for retrieving various kinds of information related to the
            // application packages that are currently installed on the device.
            // You can find this class through getPackageManager().
            val pm = ctx.packageManager
            // getPackageInfo(String packageName, int flags)
            // Retrieve overall information about an application package that is
            // installed on the system.
            // public static final int GET_ACTIVITIES
            // Since: API Level 1 PackageInfo flag: return information about
            // activities in the package in activities.
            val pi = pm.getPackageInfo(ctx.packageName, PackageManager.GET_ACTIVITIES)
            if (pi != null) {
                // public String versionName The version name of this package,
                // as specified by the <manifest> tag's versionName attribute.
                mDeviceCrashInfo[VERSION_NAME] = if (pi.versionName == null) "not set" else pi.versionName
                // public int versionCode The version number of this package,
                // as specified by the <manifest> tag's versionCode attribute.
                mDeviceCrashInfo[VERSION_CODE] = pi.versionCode
            }
        } catch (e: NameNotFoundException) {
            Log.e(TAG, "Error while collect package info", e)
        }

        // 使用反射来收集设备信息.在Build类中包含各种设备信息,
        // 例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
        // 返回 Field 对象的一个数组，这些对象反映此 Class 对象所表示的类或接口所声明的所有字段
        val fields = Build::class.java.declaredFields
        for (field in fields) {
            try {
                // setAccessible(boolean flag)
                // 将此对象的 accessible 标志设置为指示的布尔值。
                // 通过设置Accessible属性为true,才能对私有变量进行访问，不然会得到一个IllegalAccessException的异常
                field.isAccessible = true
                mDeviceCrashInfo[field.name] = field.get(null)
                if (DEBUG) {
                    Log.d(TAG, field.name + " : " + field.get(null))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error while collect crash info", e)
            }

        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return
     */
    @Synchronized
    fun saveCrashInfoToFile(ex: Throwable, filename: String = "", append: Boolean = false): String? {
        val info = StringWriter()
        val printWriter = PrintWriter(info)
        // printStackTrace(PrintWriter s)
        // 将此 throwable 及其追踪输出到指定的 PrintWriter
        ex.printStackTrace(printWriter)

        // getCause() 返回此 throwable 的 cause；如果 cause 不存在或未知，则返回 null。
        var cause: Throwable? = ex.cause
        while (cause != null) {
            cause.printStackTrace(printWriter)
            cause = cause.cause
        }

        printWriter.write("\r\n\r\n")
        // toString() 以字符串的形式返回该缓冲区的当前值。
        val result = info.toString()
        printWriter.close()
        mDeviceCrashInfo[STACK_TRACE] = result
        try {
            val timestamp = System.currentTimeMillis()
            val fileName: String
            fileName = if (filename.isBlank())
                "crash-$timestamp$CRASH_REPORTER_EXTENSION"
            else
                "crash-$filename$CRASH_REPORTER_EXTENSION"
            val file = File(crashDir, fileName)
            if (!file.exists()) {
                file.createNewFile()
            }
            // 保存文件
            val trace = FileOutputStream(file, append)
            mDeviceCrashInfo.store(trace, "")
            trace.flush()
            trace.close()
            return fileName
        } catch (e: Exception) {
            Log.e(TAG, "an error occured while writing report file...", e)
        }

        return null
    }

    @Synchronized
    fun saveLog(content: String, filename: String = "sampleLog", append: Boolean = true): String? {
        val date = Date().format("yyyyMMdd")
        try {
            val timestamp = System.currentTimeMillis()
            val fileName: String
            fileName = if (filename.isBlank())
                "crash-$timestamp$CRASH_REPORTER_EXTENSION"
            else
                "$filename$date$CRASH_REPORTER_EXTENSION"
            val file = File(crashDir, fileName)
            if (!file.exists()) {
                file.createNewFile()
            }
            // 保存文件
            val trace = FileWriter(file, append)
            trace.write("${Date().format()}  $content \r\n")
            trace.flush()
            trace.close()
            return fileName
        } catch (e: Exception) {
            Log.e(TAG, "an error occured while writing report file...", e)
        }

        return null
    }

    interface OnCrashListener {
        fun onCrash(context: Context?, thread: Thread, ex: Throwable)
    }

    companion object {
        /**
         * Debug Log Tag
         */
        const val TAG = "CrashHandler"
        /**
         * 是否开启日志输出, 在Debug状态下开启, 在Release状态下关闭以提升程序性能
         */
        const val DEBUG = true
        private const val SDCARD_LOG_FILE_SAVE_DAYS: Long = 7
        /**
         * CrashHandler实例
         */
        private var INSTANCE: CrashHandler? = null
        private const val VERSION_NAME = "versionName"
        private const val VERSION_CODE = "versionCode"
        private const val STACK_TRACE = "STACK_TRACE"
        private const val SAMPLE_LOG = "SAMPLE_LOG"
        /**
         * 错误报告文件的扩展名
         */
        private const val CRASH_REPORTER_EXTENSION = ".txt"

        /**
         * 获取CrashHandler实例 ,单例模式
         */
        val instance: CrashHandler
            get() {
                if (INSTANCE == null) INSTANCE = CrashHandler()
                return INSTANCE!!
            }

        fun delLogFileWeekAgo(dir: File) {
            val files = dir.listFiles() ?: return
            val cur = System.currentTimeMillis() - SDCARD_LOG_FILE_SAVE_DAYS * 24 * 60 * 60 * 1000
            for (f in files) {
                if (f.lastModified() < cur) {
                    f.delete()
                }
            }
        }
    }
}
