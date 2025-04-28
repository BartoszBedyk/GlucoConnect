package pl.example.aplikacja.Screens

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import kotlinx.coroutines.launch
import pl.example.aplikacja.formatUnit
import pl.example.aplikacja.removeQuotes
import pl.example.aplikacja.viewModels.UserProfileViewModel
import pl.example.networkmodule.apiData.UserResult
import pl.example.networkmodule.apiData.enumTypes.UserType
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.clearToken
import pl.example.networkmodule.getToken

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(navController: NavController) {

    val context = LocalContext.current
    val apiProvider = remember { ApiProvider(context) }
    val decoded: DecodedJWT = remember { JWT.decode(getToken(context)) }
    val id = removeQuotes(decoded.getClaim("userId").toString())
    val viewModel: UserProfileViewModel = remember {
        UserProfileViewModel(apiProvider, id)
    }
    var showDialog by remember { mutableStateOf(false) }
    var showDialogObserver by remember { mutableStateOf(false) }
    var showDialogObservator by remember { mutableStateOf(false) }
    var showAcceptDialog by remember { mutableStateOf(false) }
    var selectedObserver by remember { mutableStateOf<UserResult?>(null) }
    val healthy by viewModel.healthy.collectAsState()


    if (isNetworkAvailable(context) && healthy) {
        var codeImput by remember { mutableStateOf("") }
        val userData = viewModel.userData.collectAsState()
        val observed = viewModel.observed.collectAsState()
        val obseredUser = viewModel.observedUser.collectAsState()

        val accepted = viewModel.observatorsAccepted.collectAsState()
        val unAccepted = viewModel.observatorsUnAccepted.collectAsState()

        Log.e("UNaccepted", unAccepted.value?.size.toString())
        Log.e("Accepted", accepted.value?.size.toString())
        val fontSize = 20
        val coroutineScope = rememberCoroutineScope()
        val prefUnit = userData.value?.prefUint?.let { formatUnit(it) }
        val clipboardManager = LocalClipboardManager.current

        Box(Modifier.fillMaxSize()) {
            Text(
                text = "Dane użytkownika",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 32.dp, bottom = 16.dp),
                fontSize = 32.sp,
                fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                color = MaterialTheme.colorScheme.primary
            )
        }




        Column(
            Modifier.padding(top = 64.dp, start = 16.dp, end = 16.dp)
        ) {
//            TextRow(
//                label = "ID użytkownika", value = userData.value?.id.toString(), fontSize = fontSize
//            )
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

            if (showDialog) {
                Dialog(
                    onDismissRequest = { showDialog = false },
                ) {
                    val code = id.take(5) + id.takeLast(5)
                    Card(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)) {
                            Text("Kod dostępu do profilu.", modifier = Modifier.align(Alignment.CenterHorizontally))

                            Text(text = code, modifier = Modifier.clickable {
                                clipboardManager.setText(AnnotatedString(code))
                            }.align(Alignment.CenterHorizontally))

                            Text("Dotknij by skopiować kod.", modifier = Modifier.align(Alignment.CenterHorizontally), color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)

                        }
                    }
                }
            }
            if (showDialogObserver) {
                Dialog(
                    onDismissRequest = { showDialogObserver = false },
                ) {
                    Card(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            Modifier
                                .padding(16.dp)
                                .align(Alignment.CenterHorizontally)) {
                            Text("Podaj kod dostępu do profilu.")
                            TextRowEdit(
                                label = "Kod dostępu",
                                value = codeImput,
                                onValueChange = { codeImput = it },
                                fontSize = 20
                            )
                            Button(onClick = {
                                coroutineScope.launch {
                                    viewModel.observe(codeImput.take(5), codeImput.takeLast(5))
                                    if (observed.value != null) {
                                        viewModel.observeUser(userData.value?.id.toString(),
                                            observed.value!!.id.toString()
                                        )
                                        if(obseredUser.value != null){
                                            showDialog = false
                                            showDialogObserver = false
                                            showDialogObservator = true
                                            navController.navigate("main_screen")
                                        }
                                        showDialog = false
                                        showDialogObserver = false
                                        showDialogObservator = true

                                    } else {
                                        //
                                    }
                                }
                            }) {
                                Text("Dodaj")
                            }
                        }
                    }
                }
            }

            if (showDialogObservator)
                Dialog(
                    onDismissRequest = { showDialogObservator = false },
                ) {
                    Card(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            Modifier
                                .padding(16.dp)
                                .align(Alignment.CenterHorizontally)) {
                            Text("Dodano użytkownika")
                            TextRow(
                                label = "Imie Nazwisko",
                                value = observed.value?.firstName.toString() + " " + observed.value?.lastName.toString()
                            )
                            //TextRow(label = "Email", value = observed.value?.email.toString())
                        }
                    }


                }



            Box(
                Modifier
                    .fillMaxSize()
                    .padding(bottom = 32.dp)
            ) {
                Column() {
                    if (userData.value?.type == UserType.PATIENT) {
                        ExtendedFloatingActionButton(
                            onClick = { showDialog = true },
                            icon = { Icon(Icons.Filled.Person, "Udostępnij kod profilu.") },
                            text = { Text(text = "Udostępnij profil") },
                            modifier = Modifier.padding(16.dp)
                        )
                        Column {
                            if(accepted.value?.size != 0){
                                Text(text = "Obserwatorzy zaakceptowani")
                            }
                            LazyColumn {
                                items(accepted.value?.size ?: 0) { index ->
                                    val observer = accepted.value?.get(index)
                                    observer?.let {
                                        it.firstName?.let { it1 ->
                                            Text(
                                                text = it1,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        selectedObserver = it
                                                        showAcceptDialog = true
                                                    }
                                                    .padding(16.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            if(unAccepted.value?.size != 0){
                                Text(text = "Obserwatorzy niezaakceptowani")
                            }
                            LazyColumn {
                                items(unAccepted.value?.size ?: 0) { index ->
                                    val observer = unAccepted.value?.get(index)
                                    observer?.let {
                                        it.firstName?.let { it1 ->
                                            Text(
                                                text = it1,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        selectedObserver = it
                                                        showAcceptDialog = true
                                                    }
                                                    .padding(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (showAcceptDialog && selectedObserver != null) {
                            AlertDialog(
                                onDismissRequest = { showAcceptDialog = false },
                                title = { Text(text = "Akceptacja obserwatora") },
                                text = { Text(text = "Czy chcesz zaakceptować obserwatora ${selectedObserver!!.firstName + " " + selectedObserver!!.lastName}?") },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            selectedObserver?.let {
                                                viewModel.accept(
                                                    selectedObserver!!.id.toString(),
                                                    userData.value?.id.toString()
                                                )
                                            }
                                            showAcceptDialog = false
                                        }
                                    ) {
                                        Text("Zaakceptuj")
                                    }
                                },
                                dismissButton = {
                                    Button(
                                        onClick = {
                                            selectedObserver?.let {
                                                viewModel.unAccept(
                                                    selectedObserver!!.id.toString(),
                                                    userData.value?.id.toString()
                                                )
                                            }
                                            showDialog = false
                                            showAcceptDialog = false
                                        }
                                    ) {
                                        Text("Odrzuć")
                                    }
                                }
                            )
                        }
                    } else if (userData.value?.type == UserType.OBSERVER) {
                        ExtendedFloatingActionButton(
                            onClick = { showDialogObserver = true },
                            icon = { Icon(Icons.Filled.Person, "Dodaj profil") },
                            text = { Text(text = "Dodaj profil do obserwacji") },
                            modifier = Modifier.padding(16.dp)
                        )

                    }


                }

                Column(Modifier.align(Alignment.BottomCenter)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {

                        ExtendedFloatingActionButton(
                            onClick = { navController.navigate("bluetooth_permission_screen/glucometer") },
                            icon = { Icon(Icons.Filled.Settings, contentDescription = "Przycisk do ekranu bluetooth.") },
                            text = { Text(text = "Bluetooth") },
                            modifier = Modifier.weight(1f)
                        )

                        ExtendedFloatingActionButton(
                            onClick = { navController.navigate("edit_user_data_screen") },
                            icon = { Icon(Icons.Filled.Edit, contentDescription = "Przycisk do edycji danych.") },
                            text = { Text(text = "Edytuj dane") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row {
                        ExtendedFloatingActionButton(
                            onClick = {
                                clearToken(context)
                                restartApp(context)
                            },
                            icon = { Icon(Icons.Filled.Close, contentDescription = "Przycisk do logoutu") },
                            text = { Text(text = "Wyloguj się") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

            }
        }
    } else {
        Box(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Dane użytkownika",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 32.dp, bottom = 16.dp),
                    fontSize = 32.sp,
                    fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                )
                Text(
                    text = "Brak połączenia z internetem",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = 24.sp,
                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                    color = MaterialTheme.colorScheme.error
                )

                Text(
                    text = "W trybie offline nie ma dostępu do danych.",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = 18.sp,
                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Połącz się by uzyskać dane.",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = 18.sp,
                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                    color = MaterialTheme.colorScheme.error
                )
            }

            ExtendedFloatingActionButton(
                onClick = { navController.navigate("bluetooth_permission_screen/glucometer") },
                icon = { Icon(Icons.Filled.Settings, "Przycisk do ekranu bluetooth.") },
                text = { Text(text = "Bluetooth") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
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

fun restartApp(context: android.content.Context) {
    val intent = Intent(context, pl.example.aplikacja.MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
}

