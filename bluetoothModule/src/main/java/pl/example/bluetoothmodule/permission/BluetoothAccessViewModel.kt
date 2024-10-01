package pl.example.bluetoothmodule.permission

import androidx.lifecycle.ViewModel

class BluetoothAccessViewModel: ViewModel() {
    //[BLUETOOTH]
    val visiblePermissionDialogQueue = mutableListOf<String>()

    fun dismissDialog(){
        visiblePermissionDialogQueue.removeFirst()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if(isGranted && !visiblePermissionDialogQueue.contains(permission)){
            visiblePermissionDialogQueue.add(permission)
        }
    }
}