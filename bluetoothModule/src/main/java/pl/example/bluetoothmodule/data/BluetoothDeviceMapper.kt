package pl.example.bluetoothmodule.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import pl.example.bluetoothmodule.domain.BluetoothDeviceDomain


@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain(): BluetoothDeviceDomain{
    return BluetoothDeviceDomain(
        name = name,
        address = address
    )
}