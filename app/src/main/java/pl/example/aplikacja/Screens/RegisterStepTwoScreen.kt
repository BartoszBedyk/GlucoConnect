package pl.example.aplikacja.Screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import pl.example.aplikacja.UiElements.GlucoseUnitDropdownMenu
import pl.example.aplikacja.viewModels.RegistrationStepTwoScreenViewModel
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiMethods.ApiProvider

@Composable
fun RegisterStepTwoScreen(
    navController: NavHostController,
    userId: String
){
    val context = LocalContext.current
    val apiProvider = ApiProvider(context)
    val viewModel = RegistrationStepTwoScreenViewModel(apiProvider)
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var prefUnit by remember { mutableStateOf<GlucoseUnitType>(GlucoseUnitType.MG_PER_DL) }
    var expanded by remember { mutableStateOf(false) }
    var registerError by remember { mutableStateOf("") }

    Log.i("RegisterStepTwoScreen", "userId: $userId")
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
            onUnitSelected = { prefUnit = it }
        )

       Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        if(viewModel.registerStepTwo(userId, name, lastName, prefUnit.toString())){
                            registerError = ""
                            navController.navigate("main_screen")
                        } else {
                            registerError = "Rejestracja nie powiodła się."
                        }

                    } catch (e: Exception) {
                        registerError = "Wystąpił błąd: ${e.message}"
                    }
                }
            }
        ) {
          Text(text = "Uzupełnij")
       }



        if (registerError.isNotEmpty()) {
            Text(
                text = registerError,
                modifier = Modifier.padding(top = 16.dp),
                color = androidx.compose.ui.graphics.Color.Red
            )
        }
    }
    Log.i("RegisterStepTwoScreen2", "userId: $userId")




}