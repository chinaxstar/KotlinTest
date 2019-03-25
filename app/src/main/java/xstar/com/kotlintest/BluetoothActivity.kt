package xstar.com.kotlintest

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.bluetooth_list.*
import org.jetbrains.anko.dimen
import org.jetbrains.anko.toast
import xstar.com.kotlintest.recycler.BaseAdapter
import xstar.com.kotlintest.recycler.BaseVH
import xstar.com.kotlintest.recycler.OnItemLongClickListener
import xstar.com.kotlintest.recycler.find
import xstar.com.kotlintest.util.BaseActivity
import xstar.com.kotlintest.util.begPermissions
import xstar.com.kotlintest.util.majorDeviceClassString
import java.util.*


class BluetoothActivity : BaseActivity(R.layout.bluetooth_list) {


    val BLUETOOTH_PERMISSION_CODE = 0x100
    val SSP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    lateinit var bluetoothAdapter: BluetoothAdapter
    var bluetoothSocket: BluetoothSocket? = null


    val blueAdapter = BaseAdapter<BluetoothDevice> {
        itemLayout = R.layout.bluetooth_list_item_layout
        bindData = { v, p ->
            val item = datas!![p]
            val deviceName = v.find<TextView>(R.id.deviceName)
            val deviceType = v.find<TextView>(R.id.deviceType)
            val deviceMac = v.find<TextView>(R.id.deviceMac)
            deviceName.text = item.name
            deviceType.text = item.bluetoothClass.majorDeviceClassString()
            deviceMac.text = item.address
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intentFilter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND)
        registerReceiver(bluetoothReceiver, intentFilter)
        blueAdapter.datas = arrayListOf()
        bluetoothList.layoutManager = LinearLayoutManager(this)
        bluetoothList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.set(0, dimen(R.dimen.dp_5), 0, dimen(R.dimen.dp_5))
            }
        })
        bluetoothList.adapter = blueAdapter
        swipe.setOnRefreshListener {
            swipe.isRefreshing = true
            bluetoothAdapter.startDiscovery()
        }

        checkBluetoothState()


        blueAdapter.onItemLongClickListner = object : OnItemLongClickListener<BluetoothDevice> {
            override fun onItemLongClick(holder: BaseVH, position: Int, item: BluetoothDevice) {

                //蓝牙2.1之前的设备不会自动加密
//                item.createInsecureRfcommSocketToServiceRecord(SSP_UUID)
                //默认加密socket
                try {
                    bluetoothSocket = item.createRfcommSocketToServiceRecord(SSP_UUID)
                    bluetoothSocket?.connect()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        permissionResult = { s, b ->
            if (s == Manifest.permission.BLUETOOTH && b) {
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                if (bluetoothAdapter.state == BluetoothAdapter.STATE_OFF) {
                    bluetoothAdapter.enable()
                }
                bluetoothAdapter.startDiscovery()
                swipe.isRefreshing = true
            } else {
                toast("蓝牙权限未授权！")
                finish()
            }
        }
    }


    fun checkBluetoothState() {
        permissionResult?.let {
            begPermissions(Manifest.permission.BLUETOOTH, BLUETOOTH_PERMISSION_CODE, it)
        }
    }


    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    blueAdapter.datas?.let {
                        synchronized(it) {
                            val size = it.size
                            it.add(intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE))
                            blueAdapter.notifyItemRangeInserted(size, 1)
                        }
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    Log.e("${this@BluetoothActivity}", "ACTION_DISCOVERY_STARTED")
                    blueAdapter.datas?.let {
                        synchronized(it) {
                            val size = it.size
                            if (size > 0) {
                                it.clear()
                                blueAdapter.notifyItemRangeRemoved(0, size)
                            }

                        }
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    swipe.isRefreshing = false
                    Log.e("${this@BluetoothActivity}", "ACTION_DISCOVERY_FINISHED")
                }
            }

        }

    }

    override fun onBackPressed() {
        if (bluetoothAdapter.isDiscovering) {
            bluetoothAdapter.cancelDiscovery()
        } else
            finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothReceiver)
    }
}