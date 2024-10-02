package pl.example.aplikacja.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.example.aplikacja.MainActivity
import pl.example.aplikacja.elements.BluetoothAdminPermissionTextProvider
import pl.example.aplikacja.elements.BluetoothConnectAdminPermissionTextProvider
import pl.example.aplikacja.elements.BluetoothPermissionTextProvider
import pl.example.aplikacja.elements.BluetoothScanPermissionTextProvider
import pl.example.aplikacja.elements.PermissionDialog
import pl.example.bluetoothmodule.permission.BluetoothAccessViewModel
import pl.example.bluetoothmodule.permission.BluetoothScan
import pl.example.bluetoothmodule.permission.BluetoothStart
import pl.example.bluetoothmodule.permission.PermissionControl


@Composable
fun bluetoothPermissionsScreen(
    navController: NavHostController,
    activity: MainActivity
) {


    val permissionList = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN,
    )
    val bluetoothAccessViewModel = viewModel<BluetoothAccessViewModel>()

    val context = LocalContext.current
    val activity = context as? Activity
    val bluetoothStart = BluetoothStart(context)
    val bluetoothScan = BluetoothScan(bluetoothStart.bluetoothAdapter)
    bluetoothScan.scanLeDevice()

    Log.d("started", bluetoothStart.toString())

    Log.d("wyniki", bluetoothScan.foundDevices().toString())
    Thread.sleep(10_000)
    Log.d("wyniki 2", bluetoothScan.foundDevices().toString())

    val dialogQuote = bluetoothAccessViewModel.visiblePermissionDialogQueue
    val permissionControl = PermissionControl(context = context)
    //Multiple
    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            perms.keys.forEach { permission ->
                bluetoothAccessViewModel.onPermissionResult(
                    permission = permission,
                    isGranted = perms[permission] == true
                )
            }
        }
    )
    LaunchedEffect(Unit) {
        multiplePermissionResultLauncher.launch(permissionList)
    }

    var foundDevices by remember { mutableStateOf("") }
    var scanning by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PermissionStatusChecker(permissionControl.isPermissionBluetoothAdmin(), "bluetoothAdmin")
        PermissionStatusChecker(
            permissionControl.isPermissionBluetoothConnect(),
            "BluetoothConnect"
        )

        Button(onClick = {
            if (!scanning) {
                scanning = true
                bluetoothScan.scanLeDevice()
            }
        }) {
            Text(text = "Rozpocznij skanowanie")
        }

        LaunchedEffect(scanning) {
            if (scanning) {
                kotlinx.coroutines.delay(10_000)
                foundDevices = bluetoothScan.foundDevices().toString()
                scanning = false
            }
        }

        Text(text = "Znalezione urządzenia: $foundDevices")
    }



    Log.d("Dialog?", "przed")
    dialogQuote
        .reversed()
        .forEach { permission ->
            Log.d("Dialog?", " inside ")
            PermissionDialog(
                permissionTextProvider = when (permission) {
                    Manifest.permission.BLUETOOTH -> {
                        BluetoothPermissionTextProvider()
                    }

                    Manifest.permission.BLUETOOTH_ADMIN -> {
                        BluetoothAdminPermissionTextProvider()
                    }

                    Manifest.permission.BLUETOOTH_CONNECT -> {
                        BluetoothConnectAdminPermissionTextProvider()
                    }

                    Manifest.permission.BLUETOOTH_SCAN -> {
                        BluetoothScanPermissionTextProvider()
                    }

                    else -> return@forEach
                },
                isPermanentlyDeclined = !activity?.let {
                    shouldShowRequestPermissionRationale(
                        it,
                        permission
                    )
                }!!,
                onDismiss = { bluetoothAccessViewModel::dismissDialog },
                onOkClick = {
                    bluetoothAccessViewModel.dismissDialog()
                    multiplePermissionResultLauncher.launch(
                        arrayOf(permission)
                    )
                },
                onGotToSettingsClick = openAppSettings(context)
            )
        }
    Log.d("Dialog?", "po")
}


fun openAppSettings(activity: Context) {
    activity.startActivity(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", activity.packageName, null)
        )
    )
}

@Composable
fun PermissionStatusChecker(isPermissionGranted: Boolean, text: String) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(if (isPermissionGranted) Color.Green else Color.Red)
    ) {
        Text(
            text = text,
            color = Color.White,
            modifier = Modifier
        )
    }
}
