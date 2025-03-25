package pl.example.aplikacja

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.example.aplikacja.UiElements.BottomNavigationItem
import pl.example.networkmodule.apiData.enumTypes.UserType
import javax.inject.Inject


class BottomNavBarViewModel(
    private var userType: UserType
) : ViewModel() {


    val items = mutableStateListOf<BottomNavigationItem>()
    val selectedItemIndex = mutableStateOf(0)

    init {
        Log.d("BottomNavBarViewModel", "Zmienna userType po konwersji: $userType")
        loadItemsForUser()
    }

    fun updateUserType(newUserType: UserType) {
        if (newUserType != userType) {
            userType = newUserType
            items.clear()
            loadItemsForUser()
        }
    }


    private fun loadItemsForUser() {
        val newItems = when (userType) {
            UserType.ADMIN -> getAdminItems()
            UserType.OBSERVER -> getObserverItems()
            UserType.PATIENT -> getPatientItems()
            UserType.DOCTOR -> getDoctorItems()
        }
        items.clear()
        items.addAll(newItems)
    }

    private fun getAdminItems(): List<BottomNavigationItem> = listOf(
        BottomNavigationItem("admin_main_screen", Icons.Default.Home, Icons.Outlined.Home, false),
        BottomNavigationItem(
            "download_results",
            Icons.Default.KeyboardArrowDown,
            Icons.Outlined.KeyboardArrowDown,
            false
        ),
        BottomNavigationItem(
            "user_profile_screen",
            Icons.Default.Person,
            Icons.Outlined.Person,
            false
        )
    )

    private fun getObserverItems(): List<BottomNavigationItem> = listOf(
        BottomNavigationItem(
            "observer_main_screen",
            Icons.Default.Home,
            Icons.Outlined.Home,
            false
        ),
        BottomNavigationItem(
            "user_profile_screen",
            Icons.Default.Person,
            Icons.Outlined.Person,
            false
        )
    )

    private fun getPatientItems(): List<BottomNavigationItem> = listOf(
        BottomNavigationItem(
            title = "main_screen",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            hasNews = false
        ),
        BottomNavigationItem(
            title = "all_results_screen",
            selectedIcon = Icons.Filled.Favorite,
            unselectedIcon = Icons.Outlined.Favorite,
            hasNews = false
        ),
        BottomNavigationItem(
            title = "user_medication_screen",
            selectedIcon = Icons.Filled.ShoppingCart,
            unselectedIcon = Icons.Outlined.ShoppingCart,
            hasNews = false
        ),
        BottomNavigationItem(
            title = "user_profile_screen",
            selectedIcon = Icons.Filled.AccountCircle,
            unselectedIcon = Icons.Outlined.AccountCircle,
            hasNews = false
        )

    )

    private fun getDoctorItems(): List<BottomNavigationItem> = listOf(
        BottomNavigationItem("download_results", Icons.Default.Home, Icons.Outlined.Home, false),
        BottomNavigationItem(
            "user_profile_screen",
            Icons.Default.Person,
            Icons.Outlined.Person,
            false
        )
    )
}