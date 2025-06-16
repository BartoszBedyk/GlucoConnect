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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import kotlinx.coroutines.launch
import pl.example.aplikacja.UiElements.SwitchWithFoodIcon
import pl.example.aplikacja.UiElements.SwitchWithMedicationIcon
import pl.example.aplikacja.formatDateTimeSpecificLocale
import pl.example.aplikacja.parseMeasurement
import pl.example.aplikacja.removeQuotes
import pl.example.aplikacja.viewModels.AddGlucoseResultViewModel
import pl.example.bluetoothmodule.presentation.BluetoothUiState
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.getToken
import pl.example.networkmodule.requestData.ResearchResultCreate
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

    val viewModel: AddGlucoseResultViewModel = hiltViewModel()

    val glucoseConcentrationState = remember { mutableStateOf("0.0") }
    var unitState by remember { mutableStateOf<GlucoseUnitType?>(null) }
    var foodChecked by remember { mutableStateOf(false) }
    var medicationChecked by remember { mutableStateOf(false) }
    var note by remember { mutableStateOf("") }



    LaunchedEffect(Unit) {
        onStartScan()
    }

    Column {
        if (context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED || context.checkSelfPermission(
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Column {
                Text(text = "Aplikacja nie posiada uprawnień do podłączenia urządzeń Bluetooth")
                Button(
                    onClick = { openAppSettings(context) },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
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
                    state.pairedDevices,
                    state.scannedDevices,
                    onClick = onDeviceClick,
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
//                    Button(onClick = onStartScan) {
//                        Text(text = "Start scan")
//                    }
//                    Button(onClick = onStopScan) {
//                        Text(text = "Stop scan")
//                    }
                }
                if (destination == "addResult") {


                    Box {
                        LaunchedEffect(Unit) {
                            bluetoothViewModel.startScan()
                            bluetoothViewModel.loadingBluetooth
                            bluetoothViewModel.readLastMeasurement()
                        }

                        if (state.isConnecting) {
                            TopBluetoothPanel(isLoading = true)
                        }

//                        ElevatedButton(
//                            onClick = {
//                                Log.d("DOWNLOAD_TIME", "Button clicked")
//                                coroutineScope.launch {
//                                    bluetoothViewModel.readLastMeasurement()
//                                }
//                            }, modifier = Modifier
//                                .padding(16.dp)
//                                .align(Alignment.BottomCenter)
//                        ) {
//                            Text(text = "Pobierz pomiar")
//                        }


                    }
                    if (measurmentData.isNotBlank()) {

                        Card(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                parseMeasurement(measurmentData)?.date?.let {
                                    formatDateTimeSpecificLocale(
                                        it
                                    )
                                }?.let {
                                    Row(verticalAlignment = CenterVertically) {
                                        Text(
                                            text = "Data pomiaru: ",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 18.sp
                                        )
                                        Text(
                                            text = it,
                                            fontSize = 18.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            style = MaterialTheme.typography.labelMedium
                                        )

                                    }
                                }


                                Row(verticalAlignment = CenterVertically) {
                                    Text(
                                        text = "Poziom glukozy: ",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        text = parseMeasurement(measurmentData)?.result.toString(),
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.labelMedium
                                    )

                                }

                                Row(verticalAlignment = CenterVertically) {
                                    Text(
                                        text = "Po posiłku:",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 18.sp
                                    )
                                    SwitchWithFoodIcon(foodChecked) { foodChecked = it }

                                }
                                Row(verticalAlignment = CenterVertically) {
                                    Text(
                                        text = "Po lekach:",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 18.sp
                                    )
                                    SwitchWithMedicationIcon(medicationChecked) {
                                        medicationChecked = it
                                    }
                                }

                                TextRowEdit(
                                    label = "Notatka",
                                    value = note,
                                    onValueChange = { note = it },
                                    fontSize = 18,
                                    false
                                )


                                TextButton(onClick = {
                                    coroutineScope.launch {
                                        val parsedData = parseMeasurement(measurmentData)
                                        if (parsedData != null) {
                                            if (viewModel.addGlucoseResult(
                                                    ResearchResultCreate(
                                                        userId = UUID.fromString(
                                                            viewModel.USER_ID
                                                        ),
                                                        glucoseConcentration = parsedData.result,
                                                        unit = parsedData.unit,
                                                        timestamp = parsedData.date,
                                                        afterMedication = medicationChecked,
                                                        emptyStomach = foodChecked,
                                                        notes = note
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


                        }


                    }
                } else if (destination == "glucometer") {
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


    val sortedPairedDevicesList = mutableListOf<android.bluetooth.BluetoothDevice>()
    val gluco = pairedDevices.find { it.name == "Glucomaxx Connect" }
    if (gluco != null) {
        sortedPairedDevicesList.remove(gluco)
        sortedPairedDevicesList.add(0, gluco)
    }
    pairedDevices.forEach {
        if (it.name != "Glucomaxx Connect") {
            sortedPairedDevicesList.add(it)
        }
    }


    val sortedScannedDevicesList = mutableListOf<android.bluetooth.BluetoothDevice>()
    val glucometer = scannedDevices.find { it.name == "Glucomaxx Connect" }
    if (glucometer != null) {
        sortedScannedDevicesList.remove(glucometer)
        sortedScannedDevicesList.add(0, glucometer)
    }
    scannedDevices.forEach {
        if (it.name != "Glucomaxx Connect") {
            sortedScannedDevicesList.add(it)
        }
    }


    LazyColumn(modifier = modifier) {
        item {
            Text(
                text = "Sparowane urządzenia",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(sortedPairedDevicesList.take(5)) { device ->
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
        items(sortedScannedDevicesList) { device ->
            Text(text = device.name ?: "(Brak nazwy)",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(device) }
                    .padding(16.dp))
        }
    }
}

@Preview(
    name = "Standard",
    group = "First",
    device = "spec:width=1080px,height=2400px",
    showSystemUi = true
)
@Composable
fun TopBluetoothPanel(isLoading: Boolean = true, title: String = "Łączenie z urządzeniem") {
    if (!isLoading) return
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.primary),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterVertically),
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyLarge
        )
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(32.dp)
        )

    }
}