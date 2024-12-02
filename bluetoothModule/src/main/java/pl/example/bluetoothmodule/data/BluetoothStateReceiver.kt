package pl.example.bluetoothmodule.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class BluetoothStateReceiver(private val onStateChanged: (isConnected: Boolean, BluetoothDevice) -> Unit) :
    BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            BluetoothDevice.ACTION_FOUND -> {
                val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent?.getParcelableExtra(
                        BluetoothDevice.EXTRA_DEVICE,
                        BluetoothDevice::class.java
                    )

                } else {
                    intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                }

                Log.d("SCAN", "${ device?.name + device?.address }")
                when(intent?.action){
                    BluetoothDevice.ACTION_ACL_CONNECTED -> {
                        onStateChanged(true, device?: return)
                    }
                    BluetoothDevice.ACTION_ACL_DISCONNECTED ->{
                        onStateChanged(false, device?: return)
                    }
                }


            }
        }
    }
}