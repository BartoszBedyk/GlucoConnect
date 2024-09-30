package pl.example.bluetoothmodule

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.BLUETOOTH_ADMIN
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.LOCATION_HARDWARE
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.content.ContextCompat

class PermissionControl(private val context: Context) {

     fun isPermissionGranted(permission: String): Boolean{
        return ContextCompat.checkSelfPermission(context, ACCESS_BACKGROUND_LOCATION)==PERMISSION_GRANTED
    }
}