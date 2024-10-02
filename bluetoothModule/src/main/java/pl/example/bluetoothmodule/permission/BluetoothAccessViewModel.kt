package pl.example.bluetoothmodule.permission

import androidx.lifecycle.ViewModel

class BluetoothAccessViewModel: ViewModel() {
    //[BLUETOOTH]
    val permissionList = mutableListOf<String>()

    fun dismissDialog(){
        permissionList.removeFirst()
    }




}