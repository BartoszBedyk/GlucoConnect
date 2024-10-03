package pl.example.aplikacja.screens

import android.Manifest.permission.BLUETOOTH_CONNECT
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import pl.example.aplikacja.elements.PermissionDialog
import pl.example.bluetoothmodule.permission.BluetoothAccessViewModel
import pl.example.bluetoothmodule.permission.PermissionControl

@Composable
fun bluetoothPermissionsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val permissionControl = PermissionControl(context)

    val bluetoothPermissionViewModel = viewModel<BluetoothAccessViewModel>()
    val bluetoothPermission = PermissionControl.PermissionType(BLUETOOTH_CONNECT, permissionControl.isGranted(BLUETOOTH_CONNECT))

    var permissionRequested by remember { mutableStateOf(permissionControl.isGranted(bluetoothPermission)) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("PERMISSION", "Bluetooth permission granted")
            bluetoothPermission.isAgreed = true
            bluetoothPermissionViewModel.updatePermissionStatus(true)
        } else {
            Log.d("PERMISSION", "Bluetooth permission denied")
            bluetoothPermissionViewModel.updatePermissionStatus(false)
        }
    }



    LaunchedEffect(Unit) {
        if (!permissionRequested && !bluetoothPermission.isAgreed) {
            permissionControl.isGranted(bluetoothPermission)
            if (!bluetoothPermission.isAgreed) {
                permissionLauncher.launch(BLUETOOTH_CONNECT)
                permissionRequested = true
                bluetoothPermission.isAgreed = true
            }
        }
    }
    val isPermissionGranted = bluetoothPermissionViewModel.isPermissionGranted

    Column {
        PermissionStatusChecker(bluetoothPermission.permission, isPermissionGranted)
        if(!isPermissionGranted){
            Log.d("działa", "działaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
            PermissionDialog("Bluetooth", onOkClick = { openAppSettings(context) })
        }
        Button(onClick = {
            openAppSettings(context)
        }) {
            Text("Otwórz ustawienia aplikacji")
        }
    }
}

fun openAppSettings(context: Context) {
    context.startActivity(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
    )
}

@Composable
fun PermissionStatusChecker(permission: String, isAgreed: Boolean) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(if (isAgreed) Color.Green else Color.Red)
    ) {
        Text(
            text = permission,
            color = Color.White,
            modifier = Modifier
        )
    }
}
