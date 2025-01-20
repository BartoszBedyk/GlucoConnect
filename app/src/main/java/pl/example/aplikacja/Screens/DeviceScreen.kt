package pl.example.aplikacja.Screens

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.example.bluetoothmodule.presentation.BluetoothUiState

@Composable
fun DeviceScreen(
    state: BluetoothUiState,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    context: Context,
    onDeviceClick: (BluetoothDevice) -> Unit,
    onDownloadTime: () -> Unit,
    title: String
) {
    Column {

        if (context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED || context.checkSelfPermission(
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Column {
                Text(text = "Aplikacja nie posiada upranień do podłączenia urządzeń Bluetooth")
                Button(onClick = { openAppSettings(context) }) {
                    Text(text = "Nadaj uprawnienia")
                }
            }
        }


        Column {
            Text(
                text = title, fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )

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
            Box {
                ElevatedButton(
                    onClick = onDownloadTime,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Text(text = "Pobierz czas pomiaru")
                }
            }


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
                text = "Sparowane urządzenia",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(pairedDevices) { device ->
            Text(text = device.name ?: "(Brak nazwy)",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(device) }
                    .padding(16.dp))
        }
    }

    LazyColumn(modifier = modifier) {
        item {
            Text(
                text = "Wykryte urządzenia",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(scannedDevices) { device ->
            Text(text = device.name ?: "(Brak nazwy)",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(device) }
                    .padding(16.dp))
        }
    }
}