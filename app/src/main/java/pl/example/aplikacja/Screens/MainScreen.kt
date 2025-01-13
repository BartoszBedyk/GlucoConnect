package pl.example.aplikacja.Screens

import MainScreenViewModel
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import pl.example.aplikacja.UiElements.ItemView
import pl.example.aplikacja.removeQuotes
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.getToken

@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current

    val apiProvider = ApiProvider(context)

    val decoded: DecodedJWT = JWT.decode(getToken(context))
    val viewModel =
        MainScreenViewModel(apiProvider, removeQuotes(decoded.getClaim("userId").toString()))
    val items by viewModel.threeGlucoseItems.collectAsState()


//    val prefUnit by viewModel.prefUnit.collectAsState()


    LazyColumn(
        modifier = Modifier
            .padding(bottom = 0.dp)
            .padding(16.dp)
            .border(1.dp, androidx.compose.ui.graphics.Color.Magenta),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { item ->
            ItemView(item) { itemId ->
                navController.navigate("glucose_result/$itemId")
            }
        }
    }
}