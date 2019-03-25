package xstar.com.kotlintest

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import kotlinx.android.synthetic.main.activity_nfc.*
import org.jetbrains.anko.toast
import xstar.com.kotlintest.constant.C
import xstar.com.kotlintest.util.BaseActivity
import xstar.com.kotlintest.util.begPermissions
import java.nio.ByteBuffer
import kotlin.experimental.and


class NFCActivity : BaseActivity(R.layout.activity_nfc) {

    lateinit var nfcAdapter: NfcAdapter
    var mPendingIntent: PendingIntent? = null
    var mIntentFilter: Array<IntentFilter>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        permissionResult = { s, b ->
            checkNfcEnable()
        }
        permissionResult?.let {
            begPermissions(Manifest.permission.NFC, C.PERMISSION_REQUEST_CODE, it)
        }

        mPendingIntent = PendingIntent.getActivity(this, 0, Intent(this, this.javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
        val filter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        val filter2 = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        try {
            filter.addDataType("*/*")
        } catch (e: IntentFilter.MalformedMimeTypeException) {
            e.printStackTrace()
        }

        mIntentFilter = arrayOf(filter, filter2)
    }

    private fun checkNfcEnable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (nfcAdapter != null) {
                if (!nfcAdapter.isEnabled) {
                    val intent = Intent(Settings.ACTION_NFC_SETTINGS)
                    startActivity(intent)
                }
            } else toast("您的设备不支持NFC")
        } else {
            toast(getString(R.string.os_version_tio_low_no_nfc))
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            nfc_message.setText("卡号：${tagFromIntent.id.joinToString(separator = " ") { b -> b.toString(16).toUpperCase() }} \n")
            for (i in tagFromIntent.techList) {
                Log.e("NFC", "tech=$i")
            }
            val DFN_SRV = byteArrayOf(0xA0.toByte(), 0x00,
                    0x00, 0x00, 0x03, 0x86.toByte(), 0x98.toByte(),
                    0x07, 0x01)
//            val DFN_SRV = ByteArray(20)
            val isoDep = IsoDep.get(tagFromIntent)
            try {
                isoDep.connect()
                if (isoDep.isConnected) {
                    // 1.select PSF (1PAY.SYS.DDF01)
                    // 选择支付系统文件，它的名字是1PAY.SYS.DDF01。
                    val sys = "1PAY.SYS.DDF01".toByteArray()
                    var back = isoDep.transceive(getSelectCommand(sys))
                    nfc_message.append("选择支付系统：${back.joinToString(separator = " ") { b -> b.toString(16).toUpperCase() }} \n")
                    back = isoDep.transceive(getSelectCommand(DFN_SRV))
                    nfc_message.append("选择公交卡应用的名称：${back.joinToString(separator = " ") { b -> b.toString(16).toUpperCase() }} \n")
                    val ReadMoney = byteArrayOf(0x80.toByte(), // CLA Class
                            0x5C.toByte(), // INS Instruction
                            0x00.toByte(), // P1 Parameter 1
                            0x02.toByte(), // P2 Parameter 2
                            0x04.toByte())// Le
                    val Money = isoDep.transceive(ReadMoney)
                    if (Money != null && Money.size > 4) {
                        val cash = byteToInt(Money, 4)
                        val ba = cash / 100.0f
                        Log.e("money", cash.toString())
                    }
                    nfc_message.append("余额：${back.joinToString(separator = " ") { b -> b.toString(16).toUpperCase() }} \n")
                    val ReadRecord = byteArrayOf(0x00.toByte(), // CLA Class
                            0xB2.toByte(), // INS Instruction
                            0x01.toByte(), // P1 Parameter 1
                            0xC5.toByte(), // P2 Parameter 2
                            0x00.toByte())// Le
                    back = isoDep.transceive(ReadRecord)
                    nfc_message.append("交易记录：${back.joinToString(separator = " ") { b -> b.toString(16).toUpperCase() }} \n")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isoDep.close()
            }

        }
    }

    private fun byteToInt(money: ByteArray?, i: Int): Int {
        return money?.let {
            var num = 0
            for (i in it.indices) {
                num = num.shl(8)
                num = num.or(it[i].and((0x00ff).toByte()).toInt())
            }
            num
        } ?: 0
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.let { it.enableForegroundDispatch(this, mPendingIntent, mIntentFilter, arrayOf(arrayOf<String>())) }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.let { it.disableForegroundDispatch(this) }
    }

    private fun getSelectCommand(aid: ByteArray): ByteArray {
        val cmd_pse = ByteBuffer.allocate(aid.size + 6)
        cmd_pse.put(0x00.toByte()) // CLA Class
                .put(0xA4.toByte()) // INS Instruction
                .put(0x04.toByte()) // P1 Parameter 1
                .put(0x00.toByte()) // P2 Parameter 2
                .put(aid.size.toByte()) // Lc
                .put(aid).put(0x00.toByte()) // Le
        return cmd_pse.array()
    }
}