package pl.example.bluetoothmodule.permission

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.util.Log


@SuppressLint("MissingPermission")
class BluetoothScan(bluetoothAdapter : BluetoothAdapter?) {
    private val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
    private var scanning = false
    private val handler = Handler()

    //czas trwania skanowania
    private val SCAN_PERIOD: Long = 10000



     fun scanLeDevice(){
        if(!scanning){
            // stop scanning after  SCAN_PERIOD
            handler.postDelayed({
                scanning = false
                bluetoothLeScanner?.stopScan(leScanCallback)
            }, SCAN_PERIOD)
            scanning = true
            bluetoothLeScanner?.stopScan(leScanCallback)
        } else {
            scanning = false
            bluetoothLeScanner?.stopScan(leScanCallback)
        }
    }

    private var leDeviceListAdapter: LeDeviceListAdapter = LeDeviceListAdapter()
    //callback
    private val leScanCallback: ScanCallback = object : ScanCallback(){
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            if (result != null) {
                leDeviceListAdapter.addDevice(result.device)
                Log.d("BluetoothScan", "Znaleziono urządzenie: ${result.device.name} - ${result.device.address}")
            }
        }
    }

    fun foundDevices(): List<String> {

        val list = mutableListOf<String>()
        var x = leDeviceListAdapter.getCount()
        for(i in 0 .. x){
            list.add(leDeviceListAdapter.getDevice(i).toString())
        }
        return list;
    }


}