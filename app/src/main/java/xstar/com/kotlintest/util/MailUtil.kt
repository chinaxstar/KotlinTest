package xstar.com.kotlintest.util


import android.content.Context
import tzx.com.cn.logmail.MailInfo
import tzx.com.cn.logmail.MailSender
import java.io.File
import java.util.*
import java.util.Collections.sort


object MailUtil {

    private const val BIGEST_FILES = 10 * 1024 * 1024//10M

    private const val USER_INFO_IS_NULL = 0x120
    private const val PASSWD_INFO_IS_NULL = 0x121
    private const val SERVER_INFO_IS_NULL = 0x122

    private val mailAccount = mapOf(
            Pair("emailurl", "smtp.qq.com"),
            Pair("emailuser", "185437982@qq.com"),
            Pair("emailpassword", "woleigequ1989")
//            , Pair<String, String>("emailport", "993")
//            , Pair<String, String>("emailprotocol", "SSL")
    )

    fun sendMail(context: Context, content: String): Int {
        val mailInfo = MailInfo()
        val host = mailAccount["emailurl"]
        val user = mailAccount["emailuser"]
        val passwd = mailAccount["emailpassword"]
        var port = mailAccount["emailport"]
        val protocol = mailAccount["emailprotocol"]
        if (user == null) return USER_INFO_IS_NULL
        if (passwd == null) return PASSWD_INFO_IS_NULL
        if (host == null) return SERVER_INFO_IS_NULL
        if (port == null) port = "25"
        mailInfo.fromAddress = user
        mailInfo.toAddress = user
        mailInfo.mailServerHost = host
        mailInfo.mailServerPort = port
        mailInfo.username = user
        mailInfo.passwd = passwd
        mailInfo.isValidate = true
        if (MailInfo.EncryProtocol.SSL.name.equals(protocol, ignoreCase = true))
            mailInfo.protocol = MailInfo.EncryProtocol.SSL
        else if (MailInfo.EncryProtocol.TLS.name.equals(protocol, ignoreCase = true)) {
            mailInfo.protocol = MailInfo.EncryProtocol.TLS
        }

        val files = getLogFiles(context)
        val bits: Long = 0
        if (files.isNotEmpty()) {
            sort(files) { file, t1 -> (file.lastModified() - t1.lastModified()).toInt() }
            val temps = ArrayList<File>()
            for (f in files) {
                if (f.isFile && bits + f.length() < BIGEST_FILES) {
                    temps.add(f)
                }
            }
            mailInfo.attachFileNames = temps
        }
        mailInfo.subject = "上传日志"
        val sb = StringBuffer()
        sb
                .append("APP版本： ").append(context.getVersionName()).append("\r\n")
                .append("APP版本码： ").append(context.getVersionCode()).append("\r\n")
                .append("设备码： ").append(AppUtil.getDeviceID(context)).append("\r\n")
                .append("门店问题描述：").append("\n").append(content).append("\r\n")
        mailInfo.content = sb.toString()

        return MailSender().senderMail(mailInfo)
    }

    private fun getLogFiles(context: Context): List<File> {
        val logPath = CrashHandler.instance.crashDir

        val files = ArrayList<File>()
        if (logPath.exists() && logPath.isDirectory) {
            files.addAll(Arrays.asList(*logPath.listFiles()))
        }
        return files
    }

    fun sendError(code: Int): String {
        var msg = ""
        when (code) {
            MailSender.MESSAGING_OK -> msg = "发送成功！"
            MailSender.MESSAGING_ERROR -> msg = "发送失败！请检查配置信息！"
            MailSender.ADDRESS_ERROR -> msg = "邮箱地址解析错误！请检查配置信息！"
            MailSender.CHRSET_ERROR -> msg = "字符编码错误！请检查配置信息！"
            USER_INFO_IS_NULL -> msg = "邮箱账户信息未设置！"
            PASSWD_INFO_IS_NULL -> msg = "邮箱密码信息未设置！"
            SERVER_INFO_IS_NULL -> msg = "邮箱服务器地址信息未设置！"
        }
        return msg
    }
}
