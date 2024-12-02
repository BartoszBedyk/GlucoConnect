package pl.example.aplikacja.UiElements

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.example.aplikacja.BluetoothPermissionScreen
import pl.example.aplikacja.BottomNavBarViewModel
import pl.example.aplikacja.Screens.LoginScreen
import pl.example.aplikacja.Screens.LoginScreenV2
import pl.example.bluetoothmodule.presentation.BluetoothViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BottomNavigationBar(navBarViewModel: BottomNavBarViewModel, navController: NavController) {
    NavigationBar {
        navBarViewModel.items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = navBarViewModel.selectedItemIndex.intValue == index,
                onClick = {
                    navBarViewModel.selectedItemIndex.intValue = index
                    navController.navigate(item.title)
                },
                icon = {
                    BadgedBox(badge = {
                        if (item.badgeCount != null) {
                            Badge {
                                Text(text = item.badgeCount.toString())
                            }
                        } else if (item.hasNews) {
                            Badge()
                        }
                    }) {
                        Icon(
                            imageVector = if (index == navBarViewModel.selectedItemIndex.intValue) {
                                item.selectedIcon
                            } else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    }

                }
            )
        }
    }

}

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null

)

@Composable
fun Navigation(navBarViewModel: BottomNavBarViewModel, bluetoothViewModel: BluetoothViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home_screen") {
        composable("home_screen") {
            Log.d("Navigation", "Navigated to Home Screen")
            LoginScreenV2(navBarViewModel, navController)
        }
        composable("login_screen") {
            Log.d("Navigation", "Navigated to Login Screen")
            LoginScreen(navBarViewModel, navController)
        }
        composable("bluetooth_permission_screen") {
            Log.d("Navigation", "Navigated to permission Screen")
            BluetoothPermissionScreen(
                bluetoothViewModel,
                navBarViewModel,
                onDeviceConnected = {},
                navController
            )
        }
    }
}

@Composable
fun AppScaffold(navBarViewModel: BottomNavBarViewModel, bluetoothViewModel: BluetoothViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navBarViewModel, navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home_screen",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home_screen") {
                LoginScreenV2(navBarViewModel, navController)
            }
            composable("login_screen") {
                LoginScreen(navBarViewModel, navController)
            }
            composable("bluetooth_permission_screen") {
                BluetoothPermissionScreen(
                    bluetoothViewModel,
                    navBarViewModel,
                    onDeviceConnected = {},
                    navController
                )
            }
        }
    }


}

@Composable
fun MainApp(navBarViewModel: BottomNavBarViewModel, bluetoothViewModel: BluetoothViewModel) {
    AppScaffold(navBarViewModel, bluetoothViewModel)
}







