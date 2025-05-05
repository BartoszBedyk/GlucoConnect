package pl.example.aplikacja.Screens

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
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import pl.example.aplikacja.viewModels.RegistrationStepOneScreenViewModel
import pl.example.networkmodule.apiMethods.ApiProvider

@Composable
fun RegistrationScreen(navController: NavHostController){
    val context = LocalContext.current
    val apiProvider = ApiProvider(context)
    val viewModel = RegistrationStepOneScreenViewModel(apiProvider)
    val coroutineScope = rememberCoroutineScope()

    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordRepeat by remember { mutableStateOf("") }
    var registerError by remember { mutableStateOf("") }
    val snackState = remember { SnackbarHostState() }


    Box(Modifier
        .fillMaxSize())
    {
        Text(text = "Zarejestruj się", modifier = Modifier
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
            label = { Text(text = "Login") },
            placeholder = { Text(text = "Wpisz login") },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        PasswordTextField(
            password = password,
            onPasswordChange = { password = it }
        )

        OutlinedTextField(
            value = passwordRepeat,
            onValueChange = { passwordRepeat = it },
            label = { Text(text = "Powtórz hasło") },
            placeholder = { Text(text = "Powtórz hasło") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.padding(vertical = 8.dp)
        )




        Button(
            onClick = {
                if (checkPassword(password, passwordRepeat)!=null){
                    registerError = checkPassword(password, passwordRepeat).toString()
                }else
                {
                    coroutineScope.launch {
                        try {
                            val userId = viewModel.register(login, password)
                            if (!userId.isNullOrEmpty()) {
                                navController.navigate("register_step_two_screen/$userId")
                            } else {
                                registerError = "Rejestracja nie powiodła się."
                                snackState.showSnackbar("Błąd rejestracji sprawdź hasło oraz email.")
                            }
                        } catch (e: Exception) {
                            registerError = "Wystąpił błąd: ${e.message}"
                        }
                    }
                }
            }
        ) {
            Text(text = "Zarejestruj")
        }

        if (registerError.isNotEmpty()) {
            Text(
                text = registerError,
                modifier = Modifier.padding(top = 16.dp),
                color = androidx.compose.ui.graphics.Color.Red
            )
        }


    }
    Box(
        Modifier.padding(vertical = 8.dp).fillMaxSize(), contentAlignment = Alignment.BottomCenter
    ){
        Text(text = "Rejestrując się wyrażasz zgodę na umowę licencyjną", modifier = Modifier.padding(vertical = 8.dp)
            .align(Alignment.BottomCenter)
            .clickable {
                navController.navigate("licence_screen/licencyjna")
            },

            )
    }


}

fun checkPassword(password: String, passwordRepeat: String): String? {
    if (password.isEmpty() || passwordRepeat.isEmpty()) {
        return "Pola nie mogą być puste"
    }
    if (password.length < 8 || passwordRepeat.length < 8) {
        return "Hasła muszą mieć więcej niż 8 znaków"

    }
    if (!password.all { it.isLetterOrDigit() } || !passwordRepeat.all { it.isLetterOrDigit() }) {
        return "Hasła muszą zawierać litery i cyfry"
    }
    if (password != passwordRepeat) {
        return "Podane hasła nie są takie same"
    }
    if (!password.any { it.isUpperCase() } || !passwordRepeat.any { it.isUpperCase() }) {
        return "Hasła muszą zawierać co najmniej jedną wielką literę"
    }
    if (!password.any { it.isDigit() } || !passwordRepeat.any { it.isDigit() }) {
        return "Hasła muszą zawierać co najmniej jedną cyfrę"
    }
    return null
}

