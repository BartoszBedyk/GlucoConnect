package pl.example.aplikacja.Screens

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import pl.example.aplikacja.BottomNavBarViewModel
import pl.example.aplikacja.UiElements.BottomNavigationBar
import pl.example.aplikacja.UiElements.ColorSquare

@Composable
fun LoginScreenV2(navController: BottomNavBarViewModel, navController1: NavHostController){
    Text(text = "Login Screen V2")
    ColorSquare(isTrue = false)
    Log.d("LoginScreenV2", "Login Screen V2")
    //BottomNavigationBar(navController, navController1)
}