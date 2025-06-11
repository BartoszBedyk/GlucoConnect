package pl.example.aplikacja.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import pl.example.aplikacja.viewModels.AdminUserDirectViewModel
import pl.example.networkmodule.apiMethods.ApiProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import pl.example.aplikacja.UiElements.GlucoseUnitDropdownMenu
import pl.example.aplikacja.UiElements.UserTypeDropdownMenu
import pl.example.aplikacja.formatUnit
import pl.example.aplikacja.formatUserType
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiData.enumTypes.RestrictedUserType
import pl.example.networkmodule.requestData.UnitUpdate
import java.util.UUID


@Composable
fun AdminUserDirectScreen(
    userId: String,
    navController: NavController,
    viewModel: AdminUserDirectViewModel = hiltViewModel()
) {


    val user = viewModel.userData.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var showDialogTypeChange by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var newPassword by remember { mutableStateOf("") }
    var prefUnit by remember { mutableStateOf<GlucoseUnitType>(GlucoseUnitType.MG_PER_DL) }
    var typeState by remember { mutableStateOf<RestrictedUserType?>(null) }


    val userType by viewModel.userType.collectAsState()
    prefUnit = user.value?.prefUnit ?: GlucoseUnitType.MG_PER_DL

    LaunchedEffect(userType) {
        if (typeState == null) {
            typeState = userType
        }
    }


    Column(Modifier.padding(16.dp)) {
        Card(Modifier.padding(8.dp)) {
            Column(Modifier.padding(12.dp)) {
                TextRow("Imię", user.value?.firstName ?: "Brak", fontSize = 18)
                TextRow("Nazwisko", user.value?.lastName ?: "Brak", fontSize = 18)
                TextRow("Email", user.value?.email ?: "Brak", fontSize = 18)
                TextRow("ID", user.value?.id.toString(), fontSize = 18)
                TextRow("Jednostka", formatUnit(prefUnit), fontSize = 18)
                user.value?.type?.let { formatUserType(it) }
                    ?.let { TextRow("Typ użytkownika", it, fontSize = 18) }
                TextRow(
                    "Zablokowany",
                    if (user.value?.isBlocked == true) "Zablokowany" else "Odblokowany",
                    fontSize = 18
                )
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.Default.Lock,
                    contentDescription = if (user.value?.isBlocked == true) "Zablokowany" else "Odblokowany",
                    tint = if (user.value?.isBlocked == true) Color.Red else Color.Green,
                )


            }
        }

        Row(Modifier.padding(16.dp)) {
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Zmień jednostkę")
            }

            if (user.value?.isBlocked == true) {
                ExtendedFloatingActionButton(onClick = {
                    viewModel.viewModelScope.launch {
                        viewModel.unblcokUser()
                    }
                }, modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)) {
                    Text("Odblokuj")
                }
            } else {
                ExtendedFloatingActionButton(onClick = {
                    viewModel.viewModelScope.launch {
                        viewModel.blockUser()
                    }
                }, modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)) {
                    Text("Zablokuj")
                }
            }
        }
        Row(Modifier.padding(16.dp)) {
            ExtendedFloatingActionButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Usuń użytkownika")
            }
            ExtendedFloatingActionButton(
                onClick = { showChangePasswordDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Zmień hasło")
            }
        }
        Row(Modifier.padding(16.dp)) {
            ExtendedFloatingActionButton(
                onClick = { showDialogTypeChange = true },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Zmień typ użytkownika")
            }

        }

    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Zmień typ jednostki pomiarowej.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    GlucoseUnitDropdownMenu(
                        selectedUnit = prefUnit,
                        onUnitSelected = { prefUnit = it },
                        label = "Jednostka stężenia glukozy"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = {
                            viewModel.viewModelScope.launch {
                                viewModel.updateUnit(UnitUpdate(UUID.fromString(userId), prefUnit))
                                showDialog = false
                            }
                        }) {
                            Text("Zmień")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(onClick = { showDialog = false }) {
                            Text("Anuluj")
                        }
                    }
                }
            }
        }
    }

    if (showDialogTypeChange) {
        Dialog(onDismissRequest = { showDialogTypeChange = false }) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Zmień typ użytkownika.", style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    UserTypeDropdownMenu(
                        selectedUnit = typeState ?: RestrictedUserType.BRAK,
                        onUnitSelected = { typeState = it },
                        label = "Typ użytkownika"
                    )


                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = {
                            viewModel.viewModelScope.launch {
                                viewModel.updateType(typeState.toString())
                                showDialogTypeChange = false
                            }
                        }) {
                            Text("Zmień")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(onClick = { showDialogTypeChange = false }) {
                            Text("Anuluj")
                        }
                    }
                }
            }
        }
    }

    if (showChangePasswordDialog) {
        Dialog(onDismissRequest = { showChangePasswordDialog = false }) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Zmień hasło użytkownika", style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Nowe hasło") }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = {
                            viewModel.viewModelScope.launch {
                                viewModel.resetPassword(newPassword)
                                showChangePasswordDialog = false
                            }
                        }) {
                            Text("Zmień")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(onClick = { showChangePasswordDialog = false }) {
                            Text("Anuluj")
                        }
                    }
                }
            }
        }
    }


    if (showDeleteDialog) {
        Dialog(onDismissRequest = { showDeleteDialog = false }) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Usuń użytkownika", style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Czynność jest nieodwracalna. Czy na pewno chcesz to zrobić?")

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = {
                            viewModel.viewModelScope.launch {
                                viewModel.deleteUser()
                                showDeleteDialog = false
                                navController.popBackStack()
                            }
                        }) {
                            Text("Usuń")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(onClick = { showDeleteDialog = false }) {
                            Text("Anuluj")
                        }
                    }
                }
            }
        }
    }


}