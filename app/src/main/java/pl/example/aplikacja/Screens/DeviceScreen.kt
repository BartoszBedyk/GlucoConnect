package pl.example.aplikacja.Screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.example.aplikacja.UiElements.BottomNavigationBar
import pl.example.aplikacja.UiElements.ColorSquare
import pl.example.bluetoothmodule.presentation.BluetoothUiState
import kotlin.reflect.KFunction0

@Composable
fun DeviceScreen(
    state: BluetoothUiState,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    context: Context,
    onDeviceClick: (android.bluetooth.BluetoothDevice) -> Unit,
    onDownloadTime: KFunction0<Unit>
) {

    if (context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED || context.checkSelfPermission(
            Manifest.permission.BLUETOOTH_SCAN
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        Button(onClick = { openAppSettings(context) }) {
            Text(text = "Go to settings")
        }
    }
    Column {
        Text("BLUETOOTH_CONNECT")
        ColorSquare((context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED))
        Text("SCAN")
        ColorSquare((context.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED))
        Text("BLUETOOTH")
        ColorSquare((context.checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED))
        Text("BLUETOOTH_ADMIN")
        ColorSquare((context.checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED))
        Text("BLUETOOTH_PRIVILEGED")
        ColorSquare((context.checkSelfPermission(Manifest.permission.BLUETOOTH_PRIVILEGED) == PackageManager.PERMISSION_GRANTED))
    }


    Column {
        BluetoothDeviceList(
            state.pairedDevices, state.scannedDevices,
            onClick = onDeviceClick,
            Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = onStartScan) {
                Text(text = "Start scan")
            }
            Button(onClick = onStopScan) {
                Text(text = "Stop scan")
            }


        }

        Button(onClick =  onDownloadTime) {
            Text(text = "Pobierz czas pomiaru")
        }
    }
    //BottomNavigationBar()

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
@SuppressLint("MissingPermission")
fun BluetoothDeviceList(
    pairedDevices: List<android.bluetooth.BluetoothDevice>,
    scannedDevices: List<android.bluetooth.BluetoothDevice>,
    onClick: (android.bluetooth.BluetoothDevice) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        item {
            Text(
                text = "Paired device",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(pairedDevices) { device ->
            Text(text = device.name ?: "(NO NAME)",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(device) }
                    .padding(16.dp))
        }
    }

    LazyColumn(modifier = modifier) {
        item {
            Text(
                text = "Scanned device",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(scannedDevices) { device ->
            Text(text = device.name ?: "(NO NAME)",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(device) }
                    .padding(16.dp))
        }
    }
}