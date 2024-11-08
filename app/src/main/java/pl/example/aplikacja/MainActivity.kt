package pl.example.aplikacja

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import pl.example.aplikacja.ui.theme.AplikacjaTheme
import pl.example.bluetoothmodule.presentation.BluetoothViewModel
import pl.example.aplikacja.Screens.DeviceScreen
import pl.example.bluetoothmodule.domain.BLEScanner
import pl.example.bluetoothmodule.domain.MyBleManager
import pl.example.networkmodule.KtorClient
import pl.example.networkmodule.ResearchResult

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var bleScanner: BLEScanner
    private var myBleManager: MyBleManager? = null

    private val ktorClient = KtorClient()

    private val bluetoothManager by lazy {
        applicationContext.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bleScanner = BLEScanner(this)
        val enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            Log.d("PER", "Bluetooth act")
        }

        bleScanner.startScan()


        val permissionLauncher = registerForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { perms ->
            val canEnableBluetooth = perms[Manifest.permission.BLUETOOTH_CONNECT] == true
            if (canEnableBluetooth && !isBluetoothEnabled) {
                enableBluetoothLauncher.launch(
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                )
            }

        }



        enableEdgeToEdge()
        setContent {
            var measurment by remember{
                mutableStateOf<ResearchResult?>(null)
            }


            LaunchedEffect(key1 = Unit, block = {
                delay(5000)
                measurment = ktorClient.getResearchResultsById("19c21026-7d92-478e-bee3-e8f2d59f2d8e")})

            AplikacjaTheme {

                val bluetoothViewModel: BluetoothViewModel by viewModels()
                val state by bluetoothViewModel.state.collectAsState()
                val context = LocalContext.current



                LaunchedEffect(true) {
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

                LaunchedEffect(key1 = state.errorMessage) {
                    state.errorMessage?.let{ message -> {
                        Toast.makeText(
                            applicationContext,
                            message,
                            Toast.LENGTH_LONG
                        ).show()
                    }}
                }

                LaunchedEffect(key1 = state.isConnected) {
                    if(state.isConnected){
                        Toast.makeText(
                            applicationContext,
                            "You're connected!",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }





                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {

                    measurment?.toString()?.let { Text(text = it) }
                    Log.d("MainActivity", "Wynik: ${measurment?.id}")

                    when{
                        state.isConnecting ->{
                            Column(modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center){
                                CircularProgressIndicator()
                                Text(text = "Connecting...")
                            }
                        }
                        else -> {
                            DeviceScreen(
                                state = state,
                                onStartScan = { bleScanner.startScan() },
                                onStopScan = { bleScanner.stopScan() },
                                context = applicationContext,
                                onDeviceClick = { device ->
                                    run { connectToDevice(device) }
                                },
                                onDownloadTime = bluetoothViewModel::readMeasurementTime
                            )
                        }
                    }



                }



                Handler(Looper.getMainLooper()).postDelayed({
                    bleScanner.stopScan()
                    val foundDevices = bleScanner.getScanResults()
                    @SuppressLint("MissingPermission")
                    for (device in foundDevices) {
                        Log.d("MainActivity", "Urządzenie: ${device.name} - ${device.address}")
                    }
                    if (foundDevices.isNotEmpty()) {
                        //val deviceToConnect = foundDevices[0]
                        //connectToDevice(deviceToConnect)
                    }

                }, 10000)






            }
        }




    }

    @SuppressLint("MissingPermission")
    private fun fetchMeasurementData() {
        myBleManager?.fetchLastMeasurement { dateTime, result ->
            if (dateTime != null && result != null) {
                Log.d("MainActivity", "Data i czas pomiaru: $dateTime, Wynik: $result")
            } else {
                Log.e("MainActivity", "Nie udało się pobrać danych pomiarowych")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun connectToDevice(device: BluetoothDevice) {
        myBleManager = MyBleManager(this)

        myBleManager?.connect(device)
            ?.retry(3, 100)
            ?.done {
                Log.d("MainActivity", "Połączono z urządzeniem: ${device.name}")
                fetchMeasurementData()
            }
            ?.fail { _, _ ->
                Log.e("MainActivity", "Nie udało się połączyć z urządzeniem: ${device.name}")
            }
            ?.enqueue()
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AplikacjaTheme {
        Greeting("Android")
    }
}


