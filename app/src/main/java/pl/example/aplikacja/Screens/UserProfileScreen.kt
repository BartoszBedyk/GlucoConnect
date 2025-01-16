package pl.example.aplikacja.Screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import pl.example.aplikacja.formatUnit
import pl.example.aplikacja.removeQuotes
import pl.example.aplikacja.viewModels.UserProfileViewModel
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.getToken

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(navController: NavController) {

    val context = LocalContext.current
    val apiProvider = remember { ApiProvider(context) }
    val decoded: DecodedJWT = remember { JWT.decode(getToken(context)) }
    val viewModel: UserProfileViewModel = remember {
        UserProfileViewModel(apiProvider, removeQuotes(decoded.getClaim("userId").toString()))
    }

    val userData = viewModel.userData.collectAsState()
    val fontSize = 20

    val prefUnit = userData.value?.prefUint?.let { formatUnit(it) }


    Box(Modifier.fillMaxSize()) {
        Text(
            text = "Dane użytkownika",
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 32.dp, bottom = 16.dp ),
            fontSize = 32.sp,
            fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
        )
    }

    Log.e("TAG", "ID: ${userData.value?.id}")



    Column(
        Modifier.padding(top = 64.dp, start = 16.dp, end = 16.dp)
    ) {
        TextRow(
            label = "ID użytkownika", value = userData.value?.id.toString(), fontSize = fontSize
        )
        TextRow(
            label = "Adres email", value = userData.value?.email.toString(), fontSize = fontSize
        )
        TextRow(
            label = "Dane personalne",
            value = userData.value?.firstName.toString() + " " + userData.value?.lastName.toString(),
            fontSize = fontSize
        )
        if (prefUnit != null) {
            TextRow(
                label = "Jednostka stęzenia glukozy", value = prefUnit, fontSize = fontSize
            )
        }

        Box(Modifier.fillMaxSize().padding(bottom = 32.dp)) {
            ExtendedFloatingActionButton(
                onClick = {navController.navigate("bluetooth_permission_screen")},
                icon = { Icon(Icons.Filled.Settings, "Przycisk do ekranu bluetooth.") },
                text = { Text(text = "Bluetooth") },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
            )
            ExtendedFloatingActionButton(
                onClick = {navController.navigate("edit_user_data_screen")},
                icon = { Icon(Icons.Filled.Edit, "Przycisk do edycji danych.") },
                text = { Text(text = "Edytuj dane") },
                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
            )
        }
    }

}


@Composable
fun TextRow(label: String, value: String, fontSize: Int) {
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
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = fontSize.sp
        )
    }
}