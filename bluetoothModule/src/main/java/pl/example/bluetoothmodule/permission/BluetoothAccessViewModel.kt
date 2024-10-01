package pl.example.bluetoothmodule.permission

import androidx.lifecycle.ViewModel

class BluetoothAccessViewModel: ViewModel() {
    //[BLUETOOTH]
    val visiblePermissionDialogQueue = mutableListOf<String>()

    fun dismissDialog(){
        visiblePermissionDialogQueue.removeLast()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if(isGranted){
            visiblePermissionDialogQueue.add(0, permission)
        }
    }
}