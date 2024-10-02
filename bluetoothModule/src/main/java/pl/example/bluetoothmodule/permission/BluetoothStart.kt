package pl.example.bluetoothmodule.permission

import BluetoothActivator.Companion.REQUEST_ENABLE_BT
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService

class BluetoothStart(private val context: Context) {
     val bluetoothManager: BluetoothManager? =
        context.getSystemService(BluetoothManager::class.java)
    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter;

    val isSupported = isSupported(bluetoothAdapter)
    val handle = handleEnablance(bluetoothAdapter, context)


}

fun isSupported(bluetoothAdapter : BluetoothAdapter?) : Boolean{
    return if(bluetoothAdapter == null){
        Log.d("isSupported", "false");
        false
    } else {
        Log.d("isSupported", "true");
        true
    }
}

@SuppressLint("MissingPermission")
fun handleEnablance(bluetoothAdapter: BluetoothAdapter?, context : Context){
    if(bluetoothAdapter?.isEnabled == false){
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        context.startActivity(enableBtIntent)
    }
}