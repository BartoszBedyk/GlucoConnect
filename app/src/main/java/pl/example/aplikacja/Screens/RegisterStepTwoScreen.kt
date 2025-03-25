package pl.example.aplikacja.Screens

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
import pl.example.aplikacja.UiElements.GlucoseUnitDropdownMenu
import pl.example.aplikacja.UiElements.UserTypeDropdownMenu
import pl.example.aplikacja.viewModels.RegistrationStepTwoScreenViewModel
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiData.enumTypes.RestrictedUserType
import pl.example.networkmodule.apiMethods.ApiProvider

@Composable
fun RegisterStepTwoScreen(
    navController: NavHostController, userId: String
) {
    val context = LocalContext.current
    val apiProvider = ApiProvider(context)
    val viewModel = RegistrationStepTwoScreenViewModel(apiProvider)
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var prefUnit by remember { mutableStateOf<GlucoseUnitType>(GlucoseUnitType.MG_PER_DL) }
    var expanded by remember { mutableStateOf(false) }
    var registerError by remember { mutableStateOf("") }
    var typeState by remember { mutableStateOf<RestrictedUserType>(RestrictedUserType.PATIENT) }
    val snackState = remember { SnackbarHostState() }
    Box(
        Modifier.fillMaxSize()
    ) {
        Text(
            text = "Dokończ konfigurację",
            modifier = Modifier
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
            value = name,
            onValueChange = { name = it },
            label = { Text(text = "Imie") },
            placeholder = { Text(text = "Wpisz imie") },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Unspecified)
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text(text = "Nazwisko") },
            placeholder = { Text(text = "Wpisz nazwisko") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Unspecified),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        GlucoseUnitDropdownMenu(
            selectedUnit = prefUnit,
            onUnitSelected = { prefUnit = it },
            label = "Jednostka stęzenia glukozy"
        )

        UserTypeDropdownMenu(
            selectedUnit = typeState,
            onUnitSelected = { typeState = it },
            label = "Typ użytkownika"
        )


        Button(onClick = {
            coroutineScope.launch {
                try {
                    if(validateForm(name, lastName)!=null){
                        registerError =  validateForm(name, lastName).toString()
                    }else
                    {
                        if (viewModel.registerStepTwo(
                                userId, name, lastName, prefUnit.toString()
                            )
                        ) {
                            viewModel.updateType(userId, typeState.toString())
                            registerError = ""
                            navController.navigate("login_screen")
                            snackState.showSnackbar("Możesz się zalogować!")

                        } else {
                            registerError = "Rejestracja nie powiodła się."
                            snackState.showSnackbar("Pamiętaj o poprawności danych")
                        }
                    }


                } catch (e: Exception) {
                    registerError = "Wystąpił błąd: ${e.message}"
                }
            }
        }) {
            Text(text = "Zatwierdź")
        }

        if (registerError.isNotEmpty()) {
            Text(
                text = registerError,
                modifier = Modifier.padding(top = 16.dp),
                color = androidx.compose.ui.graphics.Color.Red
            )
        }
    }
}

fun validateForm(name: String, lastName: String): String?{
    if (name.isEmpty() || lastName.isEmpty()) {
        return "Pola nie mogą być puste"
    }
    if (name.length < 2 || lastName.length < 2) {
        return "Dane muszą mieć więcej niż 2 znaki"
    }
    if (!name.all { it.isLetter() } || !lastName.all { it.isLetter() }) {
        return "Imie i nazwisko muszą zawierać litery"
    }
    return null
}
