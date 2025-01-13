package pl.example.aplikacja

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.example.aplikacja.UiElements.BottomNavigationItem
import javax.inject.Inject


@HiltViewModel
class BottomNavBarViewModel @Inject constructor() : ViewModel() {
    val selectedItemIndex = mutableIntStateOf(0)

    val items = listOf(
        BottomNavigationItem(
            title = "main_screen",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            hasNews = false
        ),
        BottomNavigationItem(
            title = "user_profile_screen",
            selectedIcon = Icons.Filled.Lock,
            unselectedIcon = Icons.Outlined.Lock,
            hasNews = false
        ),
        BottomNavigationItem(
            title = "bluetooth_permission_screen",
            selectedIcon = Icons.Filled.ThumbUp,
            unselectedIcon = Icons.Outlined.ThumbUp,
            hasNews = false
        )
    )
}