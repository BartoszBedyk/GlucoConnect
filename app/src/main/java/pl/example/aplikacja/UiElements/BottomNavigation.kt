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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import pl.example.aplikacja.BluetoothPermissionScreen
import pl.example.aplikacja.BottomNavBarViewModel
import pl.example.aplikacja.Screens.EditUserDataScreen
import pl.example.aplikacja.Screens.GlucoseResultScreen
import pl.example.aplikacja.Screens.LoginScreen
import pl.example.aplikacja.Screens.LoginScreenV2
import pl.example.aplikacja.Screens.MainScreen
import pl.example.aplikacja.Screens.RegisterStepTwoScreen
import pl.example.aplikacja.Screens.RegistrationScreen
import pl.example.aplikacja.Screens.UserProfileScreen
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
    NavHost(navController = navController, startDestination = "login_screen") {
        composable("main_screen") {
            Log.d("Navigation", "Navigated to Home Screen")
            MainScreen(navController)
        }
//        composable("login_screen") {
//            Log.d("Navigation", "Navigated to Login Screen")
//            LoginScreen(navBarViewModel, navController)
//        }
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

    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry.value?.destination?.route


    val showBottomBar = when (currentDestination) {
        "login_screen", "registration_screen", "register_step_two_screen/{userId}", "register_step_two_screen" -> false
        else -> true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navBarViewModel, navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "login_screen",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("main_screen") {
                MainScreen(navController)
            }
            composable("user_profile_screen") {
                UserProfileScreen(navController)
            }
            composable("bluetooth_permission_screen") {
                LoginScreenV2(
                    bluetoothViewModel,
                    navBarViewModel,
                    onDeviceConnected = {},
                    navController
                )
            }
            composable("login_screen") {
                LoginScreen(navBarViewModel, navController)
            }
            composable("glucose_result/{itemId}") { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
                GlucoseResultScreen(itemId)
            }
            composable("registration_screen") {
                RegistrationScreen(navController)
            }
            composable("register_step_two_screen/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                RegisterStepTwoScreen( navController, userId)
            }

            composable("user_profile_screen"){
                UserProfileScreen(navController)
            }
            composable("edit_user_data_screen"){
                EditUserDataScreen(navController)
            }
        }
    }
}

@Composable
fun MainApp(navBarViewModel: BottomNavBarViewModel, bluetoothViewModel: BluetoothViewModel) {
    AppScaffold(navBarViewModel, bluetoothViewModel)
}







