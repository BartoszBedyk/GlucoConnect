import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import pl.example.bluetoothmodule.permission.PermissionControl


class BluetoothActivator(private val context: Context) {
    private val bluetoothManager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private val permissionControl = PermissionControl(context);
    private lateinit var enableBtLauncher: ActivityResultLauncher<Intent>

    fun checkBluetoothSupport(): Boolean {
        if (bluetoothAdapter == null) {
            Log.d("BTA", "Urządzenie nie wspiera bluetooth")
            return false
        } else {
            Log.d("BTA", "Urządzenie wspiera bluetooth")
            return true
        }
    }

    companion object {
        const val REQUEST_ENABLE_BT = 1
    }

//    @SuppressLint("MissingPermission")
//    fun checkAndRequestBluetooth()  {
//
//
//        val bluetoothManager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
//        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
//        permissionControl.isPermissionBluetoothConnect()
//        if (bluetoothAdapter?.isEnabled == false) {
//            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//            enableBtLauncher.launch(enableBtIntent)
//            //return true
//        }
//        //return false
//    }

}