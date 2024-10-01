package pl.example.aplikacja

import BluetoothActivator
import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import pl.example.aplikacja.ui.theme.AplikacjaTheme
import pl.example.bluetoothmodule.permission.BluetoothAccessViewModel
import pl.example.bluetoothmodule.permission.PermissionControl

class MainActivity : ComponentActivity() {
    lateinit var enableBtLauncher: ActivityResultLauncher<Intent>
    val bluetoothActivator = BluetoothActivator(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val permissionControll = PermissionControl(this)




        enableEdgeToEdge()
        setContent {
            AplikacjaTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PermissionStatusChecker(
                        permissionControll.isPermissionBluetoothAdmin(),
                        "Admin"
                    )
                    PermissionStatusChecker(
                        permissionControll.isPermissionBluetoothConnect(),
                        "Connect"
                    )
                    PermissionStatusChecker(
                        bluetoothActivator.checkBluetoothSupport(),
                        "Support"
                    )
                    enableBtLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                        if (result.resultCode == RESULT_OK) {
                            Log.d("dziala", "enebled")
                        } else {
                            Log.d("nie działa", "disebled")
                        }
                    }

                    bluetoothActivator.checkAndRequestBluetooth()
                }


                }
            }
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


@Composable
fun PermissionStatusChecker(isPermissionGranted: Boolean, text: String) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .background(if (isPermissionGranted) Color.Green else Color.Red)
    ) {
        Text(
            text = text,
            color = Color.White,
            modifier = Modifier
        )
    }
}

