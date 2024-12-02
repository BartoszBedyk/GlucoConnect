package pl.example.aplikacja

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import pl.example.aplikacja.Screens.DeviceScreen
import pl.example.aplikacja.UiElements.BottomNavigationBar
import pl.example.aplikacja.UiElements.ColorSquare
import pl.example.bluetoothmodule.domain.BLEScanner
import pl.example.bluetoothmodule.domain.MyBleManager
import pl.example.bluetoothmodule.presentation.BluetoothViewModel

@SuppressLint("MissingPermission")
@Composable
fun BluetoothPermissionScreen(
    bluetoothViewModel: BluetoothViewModel,
    navBarViewModel: BottomNavBarViewModel,
    onDeviceConnected: (BluetoothDevice) -> Unit = {},
    navController: NavHostController
) {
    val context = LocalContext.current
    val bleScanner = remember { BLEScanner(context) }
    var isScanning by remember { mutableStateOf(false) }
    val state by bluetoothViewModel.state.collectAsState()


    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val bluetoothGranted = permissions[Manifest.permission.BLUETOOTH_CONNECT] == true
        if (bluetoothGranted) {
            enableBluetooth(context)
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.INTERNET
            )
        )
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(state.isConnected) {
        if (state.isConnected) {
            Toast.makeText(context, "You're connected!", Toast.LENGTH_LONG).show()
        }
    }

    when {
        state.isConnecting -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Text(text = "Connecting...")
            }
        }

        else -> {
            DeviceScreen(
                state = state,
                onStartScan = {
                    isScanning = true
                    bleScanner.startScan()
                },
                onStopScan = {
                    isScanning = false
                    bleScanner.stopScan()
                },
                context = context,
                onDeviceClick = { device ->
                    connectToDevice(context, device, onDeviceConnected)
                },
                onDownloadTime = bluetoothViewModel::readMeasurementTime
            )
        }
    }

    if (isScanning) {
        LaunchedEffect(Unit) {
            bleScanner.stopScan()
            val foundDevices = bleScanner.getScanResults()
            foundDevices.forEach { device ->
                Log.d("BluetoothScreen", "Device found: ${device.name} - ${device.address}")
            }
        }
    }
    ColorSquare(isTrue = true)

    //BottomNavigationBar(navBarViewModel, navController)
}


private fun enableBluetooth(context: Context) {
    val intent = Intent(android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE)
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }
    context.startActivity(intent)
}

@SuppressLint("MissingPermission")
private fun connectToDevice(
    context: Context,
    device: BluetoothDevice,
    onDeviceConnected: (BluetoothDevice) -> Unit
) {
    val bleManager = MyBleManager(context)

    bleManager.connect(device)
        ?.retry(3, 100)
        ?.done {
            Log.d("BluetoothScreen", "Connected to device: ${device.name}")
            onDeviceConnected(device)
        }
        ?.fail { _, _ ->
            Log.e("BluetoothScreen", "Failed to connect to device: ${device.name}")
        }
        ?.enqueue()
}
