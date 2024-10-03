package pl.example.bluetoothmodule.permission

import android.Manifest.permission.BLUETOOTH_ADMIN
import android.Manifest.permission.BLUETOOTH_ADVERTISE
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionControl(private val context: Context) {

    val permissionList = mutableListOf<PermissionType>()

    data class PermissionType(
        val permission: String,
        var isAgreed: Boolean
    )

    fun addToList(permission: String, isAgreed: Boolean) {
        permissionList.add(PermissionType(permission, isAgreed))
    }

    fun grantPermission(permissionType: PermissionType) {
        permissionType.isAgreed = true
    }

    fun dropFromList() {
        if (permissionList.isNotEmpty()) {
            permissionList.removeLast()
        }
    }

    fun isGranted(permissionType: PermissionType) : Boolean {
        if (ContextCompat.checkSelfPermission(context, permissionType.permission) == PERMISSION_GRANTED) {
            permissionList.remove(permissionType)
            Log.d("PERMISSION", "Permission is granted")
            permissionType.isAgreed = true
            return true
        } else {
            Log.d("PERMISSION", "Permission isn't granted")
            return false
        }
    }

    fun isGranted(permission: String) : Boolean {
        if (ContextCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED) {
            Log.d("PERMISSION", "Permission is granted")
            return true
        } else {
            Log.d("PERMISSION", "Permission isn't granted")
            return false
        }
    }

    fun askForPermission(permissionType: PermissionType, requestCode: Int) {
        val permission = arrayOf(permissionType.permission)

        ActivityCompat.requestPermissions(context as Activity, permission, requestCode)
    }




}
