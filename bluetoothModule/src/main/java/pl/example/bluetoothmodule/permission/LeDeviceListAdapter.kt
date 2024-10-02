package pl.example.bluetoothmodule.permission

import android.bluetooth.BluetoothDevice

class LeDeviceListAdapter {

    // Adapter for holding devices found through scanning.
    private val mLeDevices: MutableList<BluetoothDevice> = mutableListOf()

    fun addDevice(device: BluetoothDevice) {
        if (!mLeDevices.contains(device)) {
            mLeDevices.add(device)
        }
    }

    fun getDevice(position: Int): BluetoothDevice? {
        return mLeDevices.getOrNull(position)
    }

    fun clear() {
        mLeDevices.clear()
    }

    fun getCount(): Int {
        return mLeDevices.size
    }

    fun getItem(i: Int): BluetoothDevice? {
        return mLeDevices.getOrNull(i)
    }

    fun getItemId(i: Int): Long {
        return i.toLong()
    }
}
