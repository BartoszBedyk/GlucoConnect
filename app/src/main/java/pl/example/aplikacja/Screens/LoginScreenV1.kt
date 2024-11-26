package pl.example.aplikacja.Screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import pl.example.aplikacja.BottomNavBarViewModel
import pl.example.aplikacja.UiElements.BottomNavigationBar
import pl.example.aplikacja.UiElements.ColorSquare

@Composable
fun LoginScreen(navBarViewModel: BottomNavBarViewModel, navController: NavHostController){
    Column {
        Text(text = "Login Screen")

    }
    ColorSquare(isTrue = true)



    Log.d("LoginScreen", "Login Screen")
}