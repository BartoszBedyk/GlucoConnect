package pl.example.aplikacja.uiElements

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import pl.example.aplikacja.BottomNavBarViewModel
import pl.example.aplikacja.feature.addglucose.AddGlucoseResultScreen
import pl.example.aplikacja.feature.addheartbeat.AddHeartbeatResultScreen
import pl.example.aplikacja.feature.addmedication.AddUserMedicationScreen
import pl.example.aplikacja.feature.admindirect.AdminUserDirectScreen
import pl.example.aplikacja.feature.allresults.AllResultsScreen
import pl.example.aplikacja.feature.bluetooth.BluetoothPermission
import pl.example.aplikacja.feature.bluetooth.GlucometerAdminScreen
import pl.example.aplikacja.feature.edituser.EditUserDataScreen
import pl.example.aplikacja.feature.glucoseresult.GlucoseResultScreen
import pl.example.aplikacja.feature.heartbeatresult.HeartbeatResultScreen
import pl.example.aplikacja.feature.login.LicenceScreen
import pl.example.aplikacja.feature.login.LoginScreen
import pl.example.aplikacja.feature.mainadmin.AdministrationMainScreen
import pl.example.aplikacja.feature.mainobserver.ObserverMainScreen
import pl.example.aplikacja.feature.mainuser.MainScreen
import pl.example.aplikacja.feature.medication.MedicationResultScreen
import pl.example.aplikacja.feature.medicationhistory.MedicationHistoryScreen
import pl.example.aplikacja.feature.registerone.RegistrationScreen
import pl.example.aplikacja.feature.registertwo.RegisterStepTwoScreen
import pl.example.aplikacja.feature.resultsdownload.AllResultsDownload
import pl.example.aplikacja.feature.user.UserProfileScreen
import pl.example.aplikacja.feature.usermedication.UserMedicationScreen
import pl.example.bluetoothmodule.presentation.BluetoothViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BottomNavigationBar(navBarViewModel: BottomNavBarViewModel, navController: NavController) {
    NavigationBar {
        navBarViewModel.items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = navBarViewModel.selectedItemIndex.value == index,
                onClick = {
                    navBarViewModel.selectedItemIndex.value = index
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
                            imageVector = if (index == navBarViewModel.selectedItemIndex.value) {
                                item.selectedIcon
                            } else {
                                item.unselectedIcon
                            },
                            contentDescription = item.title,
                            tint = if (index == navBarViewModel.selectedItemIndex.value) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onBackground
                            },
                        )
                    }
                },
                colors = NavigationBarItemColors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    selectedIndicatorColor = Color(0xFF006D8F),
                    disabledIconColor = MaterialTheme.colorScheme.outlineVariant,
                    disabledTextColor = MaterialTheme.colorScheme.outlineVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.outlineVariant,
                    unselectedIconColor = MaterialTheme.colorScheme.outlineVariant,
                ),
            )
        }
    }
}

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null,
)

@Composable
fun AppScaffold(navBarViewModel: BottomNavBarViewModel, bluetoothViewModel: BluetoothViewModel) {
    val navController = rememberNavController()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination?.route ?: "unknown"

    Log.d("AppScaffold", "Current backstack entry: $currentBackStackEntry")
    val userId = navController.currentBackStackEntry?.arguments?.getString("userId")
    Log.d("AppScaffold", "Current destination: $currentDestination")

    LaunchedEffect(currentBackStackEntry) {
        Log.d("AppScaffold", "Updated destination: $currentDestination")
    }
    val showBottomBar = !listOf(
        "login_screen",
        "registration_screen",
        "register_step_two_screen",
        "register_step_two_screen/{userId}",
        "download_results",
        "licence_screen",
        "observer_main",
    ).contains(currentDestination)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navBarViewModel, navController)
            }
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "login_screen",
            modifier = Modifier.padding(paddingValues),
        ) {
            composable("main_screen") {
                MainScreen(navController, null)
            }
            composable("user_profile_screen") {
                UserProfileScreen(navController)
            }
            composable("bluetooth_permission_screen/{destination}") { backStackEntry ->
                val destination = backStackEntry.arguments?.getString("destination") ?: ""
                BluetoothPermission(
                    bluetoothViewModel,
                    navBarViewModel,
                    onDeviceConnected = {},
                    navController,
                    destination = destination,
                )
            }
            composable("login_screen") {
                LoginScreen(navBarViewModel, navController)
            }
            composable("glucose_result/{itemId}") { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
                GlucoseResultScreen(itemId, navController)
            }
            composable("heartbeat_result/{itemId}") { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
                HeartbeatResultScreen(itemId, navController)
            }
            composable("registration_screen") {
                RegistrationScreen(navController)
            }
            composable("register_step_two_screen/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                RegisterStepTwoScreen(navController, userId)
            }
            composable("user_profile_screen") {
                UserProfileScreen(navController)
            }
            composable("edit_user_data_screen") {
                EditUserDataScreen(navController)
            }
            composable("all_results_screen") {
                AllResultsScreen(navController)
            }
            composable("add_glucose_result") {
                AddGlucoseResultScreen(navController)
            }
            composable("add_heartbeat_result") {
                AddHeartbeatResultScreen(navController)
            }
            composable("add_glucose_result/{main}") { backStackEntry ->
                val type = backStackEntry.arguments?.getString("main") ?: ""
                AddGlucoseResultScreen(navController, type.isNotBlank())
            }
            composable("add_heartbeat_result/{main}") { backStackEntry ->
                val type = backStackEntry.arguments?.getString("main") ?: ""
                AddHeartbeatResultScreen(navController, type.isNotBlank())
            }
            composable("all_results_screen/{type}") { backStackEntry ->
                val type = backStackEntry.arguments?.getString("type") ?: ""
                AllResultsScreen(navController, type.toBoolean())
            }
            composable("user_medication_screen") {
                UserMedicationScreen(navController)
            }
            composable("medication_result/{umId}/{medicationId}") { backStackEntry ->
                val umId = backStackEntry.arguments?.getString("umId") ?: ""
                val medicationId = backStackEntry.arguments?.getString("medicationId") ?: ""
                MedicationResultScreen(umId, medicationId, navController)
            }
            composable("add_user_medication_screen") {
                AddUserMedicationScreen(navController)
            }
            composable("glucometer_admin_screen") {
                GlucometerAdminScreen(bluetoothViewModel, navController)
            }
            composable("licence_screen/{typUmowy}") { backStackEntry ->
                val typUmowy = backStackEntry.arguments?.getString("typUmowy") ?: ""
                LicenceScreen(typUmowy)
            }
            composable("download_results") {
                AllResultsDownload(navController)
            }
            composable("admin_main_screen") {
                AdministrationMainScreen(navController)
            }
            composable("admin_user_direct/{userId}") { backStackEntry ->
                AdminUserDirectScreen(
                    backStackEntry.arguments?.getString("userId") ?: "",
                    navController,
                )
            }
            composable("observer_main_screen") {
                ObserverMainScreen(navController)
            }
            composable("observer_main") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                MainScreen(navController, userId)
            }
            composable("main_screen/{userId}") {
                MainScreen(navController, userId)
            }
            composable("medication_history_screen") {
                MedicationHistoryScreen(navController)
            }
        }
    }
}

@Composable
fun MainApp(navBarViewModel: BottomNavBarViewModel, bluetoothViewModel: BluetoothViewModel) {
    AppScaffold(navBarViewModel, bluetoothViewModel)
}
