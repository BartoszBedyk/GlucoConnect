package pl.example.aplikacja.Screens

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import pl.example.aplikacja.BottomNavBarViewModel
import pl.example.aplikacja.viewModels.LoginScreenViewModel
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.getToken

@Composable
fun LoginScreen(navBarViewModel: BottomNavBarViewModel, navController: NavHostController) {
    val context = LocalContext.current

    if (getToken(context) != null) {
        navController.navigate("main_screen")
    }

    val coroutineScope = rememberCoroutineScope()
    val apiProvider = ApiProvider(context)
    val viewModel = LoginScreenViewModel(apiProvider)

    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf("") }
    var blocked by remember { mutableStateOf(false) }


    Box(Modifier
        .fillMaxSize())
    {
        Text(text = "Zaloguj się", modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(bottom = 16.dp, top = 100.dp),
            color = androidx.compose.ui.graphics.Color.White,
            fontSize = 32.sp
            )
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = login,
            onValueChange = { login = it },
            label = { Text(text = "Adres email") },
            placeholder = { Text(text = "Wpisz email") },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Hasło") },
            placeholder = { Text(text = "Wpisz hasło") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Button(
            onClick = {
                if (!blocked) {
                    blocked = true
                    loginError = ""
                    coroutineScope.launch {
                        if (isNetworkAvailable(context)) {
                            val token = viewModel.login(login, password, context)
                            if (token != null) {
                                navController.navigate("main_screen")
                            } else {
                                loginError = "Podczas logowania wystąpił błąd. Spróbuj ponownie."
                            }
                        } else {
                            loginError = "Brak połączenia z Internetem. Sprawdź połączenie i spróbuj ponownie."
                        }
                        blocked = false
                    }
                }
            },
            enabled = !blocked
        ) {
            Text(text = if (blocked) "Logowanie..." else "Zaloguj")
        }

        if (loginError.isNotEmpty()) {
            Text(
                text = loginError,
                color = androidx.compose.ui.graphics.Color.Red,
                fontSize = 16.sp,
                overflow = TextOverflow.Clip,
                maxLines = 2,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
    Text(text = "Nie masz konta? Załóż je tutaj", modifier = Modifier
        .padding(top = 16.dp)
        .padding(bottom = 32.dp)
        .align(Alignment.BottomCenter)
        .clickable { navController.navigate("registration_screen") })
    }

}

@SuppressLint("ServiceCast")
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}


