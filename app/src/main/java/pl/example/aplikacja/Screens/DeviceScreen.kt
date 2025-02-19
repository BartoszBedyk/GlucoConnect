package pl.example.aplikacja.Screens

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import kotlinx.coroutines.launch
import pl.example.aplikacja.parseMeasurement
import pl.example.aplikacja.removeQuotes
import pl.example.aplikacja.viewModels.AddGlucoseResultViewModel
import pl.example.bluetoothmodule.presentation.BluetoothUiState
import pl.example.networkmodule.getToken
import pl.example.networkmodule.requestData.ResearchResultCreate
import java.util.Date
import java.util.UUID

@Composable
fun DeviceScreen(
    state: BluetoothUiState,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    context: Context,
    onDeviceClick: (BluetoothDevice) -> Unit,
    onDownloadTime: suspend () -> String,
    bluetoothViewModel: pl.example.bluetoothmodule.presentation.BluetoothViewModel,
    title: String,
    navController: NavHostController,
    destination: String?
) {
    val coroutineScope = rememberCoroutineScope()
    val measurementResult = remember { mutableStateOf("") }
    val measurmentData by bluetoothViewModel.lastMeasurement.collectAsState()
    Log.i("SCREEN2", "measurmentData: $measurmentData")

    val measurmentData2 by bluetoothViewModel._lastMeasurement.collectAsState()
    Log.i("SCREEN2", "measurmentData2: $measurmentData2")
    val result = ""

    val decoded: DecodedJWT = JWT.decode(getToken(context))
    val viewModel =
        AddGlucoseResultViewModel(context, removeQuotes(decoded.getClaim("userId").toString()))


    Column {
        if (context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
            context.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
        ) {
            Column {
                Text(text = "Aplikacja nie posiada uprawnień do podłączenia urządzeń Bluetooth")
                Button(onClick = { openAppSettings(context) }, modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)) {
                    Text(text = "Nadaj uprawnienia")
                }
            }
        } else {

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
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
                if(destination == "addResult"){
                Box {
                    ElevatedButton(
                        onClick = {
                            Log.d("DOWNLOAD_TIME", "Button clicked")
                            coroutineScope.launch {
                                Log.d("DOWNLOAD_TIME", "Coroutine launched")
                                measurementResult.value = onDownloadTime()
                                Log.d("DOWNLOAD_TIME", "Result received: $measurementResult.value")
                                Log.d("DOWNLOAD_TIME", "Result: ${measurmentData}")
                            }
                        },
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        Text(text = "Pobierz pomiar")
                    }
                }
                    if (measurmentData.isNotBlank() ) {
                        TextButton(onClick = {
                            coroutineScope.launch {
                                val parsedData = parseMeasurement(measurmentData)
                                if (parsedData != null) {
                                    if (viewModel.addGlucoseResult(
                                            ResearchResultCreate(
                                                userId = UUID.fromString(removeQuotes(decoded.getClaim("userId").toString())),
                                                sequenceNumber = 1,
                                                glucoseConcentration = parsedData.result,
                                                unit = parsedData.unit,
                                                timestamp = parsedData.date
                                            )
                                        )
                                    ) {
                                        navController.navigate("main_screen")
                                    } else {
//
                                    }
                                }
                            }
                        }) {
                            Text(text = "Dodaj pomiar")
                        }
                    }
                }else if (destination == "glucometer"){
                    GlucometerAdminScreen(bluetoothViewModel, navController)
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