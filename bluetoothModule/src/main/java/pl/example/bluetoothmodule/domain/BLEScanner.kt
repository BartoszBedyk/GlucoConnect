package pl.example.bluetoothmodule.domain

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import pl.example.bluetoothmodule.presentation.BluetoothViewModel

class BLEScanner(private val context: Context) {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner
    private val scanResults = mutableListOf<android.bluetooth.BluetoothDevice>()

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("BLEScanner", "Permission not granted")
                return
            }
            result?.device?.let { device ->
                if (!scanResults.contains(device)) {
                    scanResults.add(device)
                    Log.d(
                        "BLEScanner",
                        "Device found: ${device.name ?: "Unknown"} - ${device.address}"
                    )
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("BLEScanner", "Scan failed with error code: $errorCode")
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("BLEScanner", "Permission not granted")
                return
            }

            for (result in results) {
                result.device?.let { device ->
                    if (!scanResults.contains(device)) {
                        scanResults.add(device)


                        Log.d(
                            "BLEScanner",
                            "Device found: ${device.name ?: "Unknown"} - ${device.address}")
                    }

                }
            }
        }


    }


    fun startScan() {
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("BLEScanner", "Permission not granted")
            return
        }
        bluetoothLeScanner?.startScan(null, buildScanSettings(), scanCallback)
        Log.d("BLEScanner", "Scan started")
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        bluetoothLeScanner?.stopScan(scanCallback)
        Log.d("BLEScanner", "Scan stopped")
    }

    fun getScanResults(): List<BluetoothDevice> {
        return scanResults
    }

    private fun buildScanSettings(): ScanSettings {
        return ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .build()
    }

}