package pl.example.aplikacja.Screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.example.aplikacja.BottomNavBarViewModel
import pl.example.aplikacja.UiElements.BottomNavigationBar
import pl.example.aplikacja.UiElements.ColorSquare
import pl.example.networkmodule.KtorClient
import pl.example.networkmodule.apiData.UserResult
import pl.example.networkmodule.apis.AuthenticationApi
import pl.example.networkmodule.apis.UserApi
import pl.example.networkmodule.requestData.UserCredentials
import pl.example.networkmodule.saveToken
import kotlin.system.measureTimeMillis

@Composable
fun LoginScreen(navBarViewModel: BottomNavBarViewModel, navController: NavHostController) {


    val context = LocalContext.current
    val ktorClient = KtorClient(context)
    val userApi = UserApi(ktorClient);
    val authApi = AuthenticationApi(ktorClient)

    val coroutineScope = rememberCoroutineScope()

    suspend fun loginUser(login: String, password: String): String? {
        val userCredentials = UserCredentials(login, password)

        try {
            val token = authApi.login(userCredentials)
            if (token != null) {
                saveToken(context, token)
            }
            Log.d("LoginScreen", "Login successful, token: $token")
            return token
//
        } catch (e: Exception) {
            Log.d("LoginScreen", "Login failed: ${e.message}")
        }
        return null
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var login by remember { mutableStateOf(TextFieldValue("")) }
        var password by remember { mutableStateOf(TextFieldValue("")) }
        var measurment by remember { mutableStateOf<UserResult?>(null) }
        var blocked by remember { mutableStateOf(false) }


        OutlinedTextField(
            value = login,
            onValueChange = { login = it },
            label = { Text(text = "Login") },
            placeholder = { Text(text = "Login") },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Hasło") },
            placeholder = { Text(text = "Hasło") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
        )

        Button(onClick = {
            coroutineScope.launch {
                loginUser(login.text, password.text)
            }
        }) {
            Text(text = "Zaloguj")
        }

        LaunchedEffect(key1 = Unit, block = {
            //delay(5000)
            try {
                measurment = userApi.getUserById("0ac023ed-9ae0-44c1-9d2c-3fe58b670112")
            } catch (e: Exception) {
                Log.d("LoginScreen", e.toString())
            }
        })



        Text(text = measurment?.email ?: "email chyba")

        LaunchedEffect(key1 = Unit, block = {
            //delay(5000)
            val duration = measureTimeMillis {
                try {
                    blocked = userApi.blockUser("0ac023ed-9ae0-44c1-9d2c-3fe58b670112")
                } catch (e: Exception) {
                    Log.d("LoginScreen", e.toString())
                }
            }
            Log.d("LoginScreen", "Operation took $duration ms")
        }
        )

        ColorSquare(isTrue = blocked)
    }






    Log.d("LoginScreen", "Login Screen")
}