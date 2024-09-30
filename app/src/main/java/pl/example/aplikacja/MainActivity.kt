package pl.example.aplikacja

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import pl.example.aplikacja.ui.theme.AplikacjaTheme
import pl.example.bluetoothmodule.BluetoothAccessViewModel
import pl.example.bluetoothmodule.PermissionControl

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val permissionControll = PermissionControl(this)
        enableEdgeToEdge()
        setContent {
            AplikacjaTheme {
                val viewModel = BluetoothAccessViewModel()
                val dialogQueue = viewModel.visiblePermissionDialogQueue

                val bluetoothPermissionResultLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                     onResult = {isGranted ->
                         viewModel.onPermissionResult(
                              permission = Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                             isGranted = isGranted
                         )
                     }
                )
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Button(onClick = {bluetoothPermissionResultLauncher.launch(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )}) {
                        Text(text = "RequestPermission")
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = {}) {
                        Text(text = "Request multiple permission")
                    }
                    PermissionStatusChecker(permissionControll.isPermissionGranted("LOCATION_HARDWARE"), "LOCATION_HARDWARE")
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

@Composable
fun ButtonBluetooth(modifier: Modifier = Modifier) {
    Button(
        onClick = {

            if (modifier == Modifier.alpha(0.5f)) {
                modifier.alpha(1f)
            } else if (modifier == Modifier.alpha(1f)) {
                modifier.alpha(0.5f)
            }
        },
        modifier = modifier
    ) {
        Text("SŁOWOOWO")
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AplikacjaTheme {
        Greeting("Android")
    }
}

@Composable
fun ColorChangingButton() {
    var isBlack by remember { mutableStateOf(true) }

    Surface(
        onClick = { isBlack = !isBlack },
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.size(100.dp)
    ) {
        Box(
            modifier = Modifier
                .background(if (isBlack) Color.Black else Color.Red)
                .size(100.dp)
        )
    }
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

