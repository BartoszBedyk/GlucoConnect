package pl.example.aplikacja.Screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import kotlinx.coroutines.launch
import pl.example.aplikacja.BottomNavBarViewModel
import pl.example.aplikacja.MainActivity
import pl.example.aplikacja.viewModels.LoginScreenViewModel
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.clearToken
import pl.example.networkmodule.getToken
import pl.example.networkmodule.saveToken
import java.util.Date

@Composable
fun LoginScreen(navBarViewModel: BottomNavBarViewModel, navController: NavHostController) {
    val context = LocalContext.current
    //saveToken(context, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJteWF1ZGllbmNlIiwiaXNzIjoibXlpc3N1ZXIiLCJ1c2VySWQiOiIwYWMwMjNlZC05YWUwLTQ0YzEtOWQyYy0zZmU1OGI2NzAxMTEiLCJ1c2VybmFtZSI6ImQuZEB3cC5wbCIsInVzZXJUeXBlIjoiT0JTRVJWRVIiLCJleHAiOjE3NDI0MDUxNTZ9.oBoivhg8ri8uRRRbnm4NYI9ieCyeqP1FPU_NYphbM2I")
    //clearToken(context)
    val coroutineScope = rememberCoroutineScope()
    val apiProvider = remember { ApiProvider(context) }
    val viewModel = remember { LoginScreenViewModel(apiProvider) }

    val healthy by viewModel.healthy.collectAsState()
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf("") }
    var blocked by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        val currentToken = getToken(context)
        Log.i("Token", "Current token: $currentToken")
        if (currentToken != null) {
            Log.i("Token", "Token istnieje.")
            val decoded: DecodedJWT = JWT.decode(currentToken)
            val expiration = decoded.expiresAt
            val now = Date()
            if (expiration != null && now.before(expiration)) {
                Log.i("Token", "Token jest ważny")
                navController.navigate("main_screen")
            } else {
                val refreshedToken = viewModel.refreshToken(context)
                if (refreshedToken != null) {
                    Log.i("Token", "Token jest odświerzony.")
                    clearToken(context)
                    saveToken(context, refreshedToken)
                    navController.navigate("main_screen")
                } else {
                    Log.i("Token", "Token wygasł i nie można go odświeżyć")
                    clearToken(context)
                    navController.navigate("login_screen")
                }
            }
        }else{
            Log.i("Token", "Brak tokena w LoginScreen")
        }
    }

    Box(
        Modifier
            .fillMaxSize()
    )
    {
        Text(
            text = "Zaloguj się", modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(bottom = 16.dp, top = 100.dp),
            color = MaterialTheme.colorScheme.primary,
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
                        if (isNetworkAvailable(context) && healthy == true) {
                            Log.i("LoginScreen", "Network is available")
                            val token = viewModel.login(login, password, context)
                            if (token != null) {
                                saveToken(context, token)
                                val intent = Intent(context, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                                //navController.navigate("main_screen")
                            } else {
                                loginError = "Podczas logowania wystąpił błąd. Spróbuj ponownie."
                            }
                        } else {
                            //saveToken(context, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJteWF1ZGllbmNlIiwiaXNzIjoibXlpc3N1ZXIiLCJ1c2VySWQiOiI1NjI1MWVhNi0zYTU3LTRmYjQtOGQ3Ni1kMWQwODg0M2Y5YTMiLCJ1c2VybmFtZSI6ImZzLmZzQHdwLnBsIiwidXNlclR5cGUiOiJQQVRJRU5UIiwiZXhwIjoxNzQ0NzIwMjcxfQ.WuCwX23OwRtAnQUs8zz2n2U8oui1IVx8gXMwI9qeL9w")
                            loginError =
                                "Brak połączenia z serwerem. Sprawdź połączenie i spróbuj ponownie."
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
                color = MaterialTheme.colorScheme.error,
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

@SuppressLint("MissingPermission")
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        @Suppress("DEPRECATION")
        val networkInfo = connectivityManager.activeNetworkInfo ?: return false
        @Suppress("DEPRECATION")
        return networkInfo.isConnected
    }
}

@SuppressLint("MissingPermission")
fun isNetworkAvailable(context: Context, healthy: Boolean): Boolean {
    if (healthy == false) return false;
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        @Suppress("DEPRECATION")
        val networkInfo = connectivityManager.activeNetworkInfo ?: return false
        @Suppress("DEPRECATION")
        return networkInfo.isConnected
    }
}



