package pl.example.aplikacja.Screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import pl.example.networkmodule.apiMethods.ApiProvider

@Composable
fun MedicationResultScreen(itemId: String) {
    val context = LocalContext.current
    val apiProvider = remember { ApiProvider(context) }

}