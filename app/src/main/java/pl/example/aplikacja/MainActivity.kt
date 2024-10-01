package pl.example.aplikacja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.example.aplikacja.screens.bluetoothPermissionsScreen
import pl.example.bluetoothmodule.permission.BluetoothAccessViewModel

class MainActivity : ComponentActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainTheme()
        }
    }
}

@Composable
fun MainTheme() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "first_screen") {
        composable("first_screen") {
            FirstScreen(navController = navController)
        }
        composable("permissions_screen"){
            bluetoothPermissionsScreen(navController, MainActivity())
        }
    }
}

@Composable
fun FirstScreen(navController: NavHostController) {
    Button(onClick = {
        navController.navigate("permissions_screen")
    }) {
        Text("Przejdź do uprawnień")
    }
}


