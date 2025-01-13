package pl.example.aplikacja.Screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import kotlinx.coroutines.launch
import pl.example.aplikacja.UiElements.GlucoseUnitDropdownMenu
import pl.example.aplikacja.removeQuotes
import pl.example.aplikacja.viewModels.EditUserViewModel
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.getToken
import pl.example.networkmodule.requestData.UpdateUserNullForm
import java.util.UUID

@Composable
fun EditUserDataScreen(navController: NavController) {

    val context = LocalContext.current
    val apiProvider = remember { ApiProvider(context) }
    val decoded: DecodedJWT = remember { JWT.decode(getToken(context)) }
    val viewModel: EditUserViewModel = remember {
        EditUserViewModel(apiProvider, removeQuotes(decoded.getClaim("userId").toString()))
    }
    val userData = viewModel.userData.collectAsState()
    val fontSize = 20
    val coroutineScope = rememberCoroutineScope()

    val name by remember { mutableStateOf(userData.value?.firstName.toString()) }
    val lastName by remember { mutableStateOf(userData.value?.lastName.toString()) }
    val email by remember { mutableStateOf(userData.value?.email.toString()) }
    var prefUnit by remember { mutableStateOf(userData.value?.prefUint) }



    Box(Modifier.fillMaxSize()) {
        Text(
            text = "Dane użytkownika",
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 32.dp, bottom = 16.dp ),
            fontSize = 32.sp,
            fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
        )
    }



    Column(
        Modifier.padding(top = 64.dp, start = 16.dp, end = 16.dp)
    ) {
        TextRow(
            label = "ID użytkownika", value = userData.value?.id.toString(), fontSize = fontSize
        )
        TextRowEdit(
            label = "Adres email", value = email, fontSize = fontSize
        )
        TextRowEdit(
            label = "Imie",
            value = name,
            fontSize = fontSize
        )
        TextRowEdit(
            label = "Nazwisko",
            value = lastName,
            fontSize = fontSize
        )

        userData.value?.prefUint?.let { GlucoseUnitType.valueOf(it.toString()) }?.let {
            Text(
                text = "Jednostka stęzenia glukozy", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontSize = (fontSize - 5).sp
            )
            prefUnit?.let {
                GlucoseUnitDropdownMenu(
                    selectedUnit = it,
                    onUnitSelected = { prefUnit = it },
                    label = ""
                )
            }
        }

        Box(
            Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp)
        ) {
            ExtendedFloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        if (viewModel.editUserData(
                                UpdateUserNullForm(
                                    UUID.fromString(
                                        removeQuotes(
                                            decoded.getClaim("userId").toString()
                                        )
                                    ),
                                    name,
                                    lastName,
                                    prefUnit.toString()
                                )
                            )
                        ) {
                            navController.navigate("user_profile_screen")
                        }
                    }
                },
                icon = { Icon(Icons.Filled.Edit, "Przycisk do zapisu danych.") },
                text = { Text(text = "Zatwierdź edycję") },
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }


}

@Composable
fun TextRowEdit(label: String, value: String, fontSize: Int) {
    var changeableValue by remember { mutableStateOf(value) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontSize = (fontSize - 5).sp

        )
        OutlinedTextField(
            value = changeableValue,
            onValueChange = { changeableValue = it },
            placeholder = { Text(text = value) },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)

        )
    }
}