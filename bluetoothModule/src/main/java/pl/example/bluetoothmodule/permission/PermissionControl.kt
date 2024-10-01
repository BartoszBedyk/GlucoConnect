package pl.example.bluetoothmodule.permission

import android.Manifest.permission.BLUETOOTH_ADMIN
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.content.ContextCompat

class PermissionControl(private val context: Context) {

     fun isPermissionBluetoothAdmin(): Boolean{
        return ContextCompat.checkSelfPermission(context, BLUETOOTH_ADMIN)==PERMISSION_GRANTED
    }

    fun isPermissionBluetoothConnect(): Boolean{
        return ContextCompat.checkSelfPermission(context, BLUETOOTH_CONNECT)==PERMISSION_GRANTED
    }
}