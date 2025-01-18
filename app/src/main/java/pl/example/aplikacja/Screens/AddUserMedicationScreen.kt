package pl.example.aplikacja.Screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import kotlinx.coroutines.launch
import pl.example.aplikacja.formatDateTimeWithoutLocale
import pl.example.aplikacja.formatDateTimeWithoutTime
import pl.example.aplikacja.removeQuotes
import pl.example.aplikacja.viewModels.AddHeartbeatViewModel
import pl.example.aplikacja.viewModels.AddUserMedicationViewModel
import pl.example.networkmodule.apiData.MedicationResult
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.getToken
import pl.example.networkmodule.requestData.CreateHeartbeatForm
import pl.example.networkmodule.requestData.CreateUserMedicationForm
import java.util.Calendar
import java.util.Date
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserMedicationScreen(navController: NavController) {

    var dose by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(Date()) }
    var endDate by remember { mutableStateOf<Date?>(null) }
    var checked by remember { mutableStateOf(false) }
    var checked2 by remember { mutableStateOf(true) }
    var openStartDateDialog by remember { mutableStateOf(false) }
    var openEndDateDialog by remember { mutableStateOf(false) }
    val snackState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val apiProvider = remember { ApiProvider(context) }
    val decoded: DecodedJWT = JWT.decode(getToken(context))
    val viewModel = AddUserMedicationViewModel(apiProvider, removeQuotes(decoded.getClaim("userId").toString()))

    var selectedMedication by remember { mutableStateOf<MedicationResult?>(null) }

    SnackbarHost(hostState = snackState, Modifier)

    Column(Modifier.padding(16.dp)) {
        MedicationDropdownExample(
            medications = viewModel.medications.collectAsState().value,
            onMedicationSelected = { selectedMedication = it }
        )

        TextRowEdit(
            label = "Dawka",
            value = dose,
            onValueChange = { dose = it },
            fontSize = 20
        )

        TextRowEdit(
            label = "Częstotliwość",
            value = frequency,
            onValueChange = { frequency = it },
            fontSize = 20
        )

        TextRowEdit(
            label = "Notatka",
            value = note,
            onValueChange = { note = it },
            fontSize = 20
        )

        Row {
            Text(text = "Data początku", modifier = Modifier.align(CenterVertically))
            Checkbox(
                checked = checked2,
                onCheckedChange = { state ->
                    checked2 = state
                    if (state) openStartDateDialog = true
                },
                modifier = Modifier.align(CenterVertically)
            )
        }

        if (checked2) {
            TextRowEdit(
                label = "Data początku:",
                value = formatDateTimeWithoutTime(startDate),
                onValueChange = {}, // Pole tylko do odczytu
                fontSize = 20
            )

            if (openStartDateDialog) {
                val datePickerState = rememberDatePickerState()
                val confirmEnabled = remember { derivedStateOf { datePickerState.selectedDateMillis != null } }

                DatePickerDialog(
                    onDismissRequest = { openStartDateDialog = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (datePickerState.selectedDateMillis != null) {
                                    startDate = Date(datePickerState.selectedDateMillis!!)
                                }
                                openStartDateDialog = false
                            },
                            enabled = confirmEnabled.value
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { openStartDateDialog = false }) {
                            Text("Anuluj")
                        }
                    }
                ) {
                    DatePicker(
                        state = datePickerState,
                        modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())
                    )
                }
            }
        }

        Row {
            Text(text = "Data końca", modifier = Modifier.align(CenterVertically))
            Checkbox(
                checked = checked,
                onCheckedChange = { state ->
                    checked = state
                    if (state) openEndDateDialog = true
                },
                modifier = Modifier.align(CenterVertically)
            )
        }

        if (checked) {
            TextRowEdit(
                label = "Data zakończenia:",
                value = endDate?.let { formatDateTimeWithoutTime(it) } ?: "",
                onValueChange = {}, // Pole tylko do odczytu
                fontSize = 20
            )

            if (openEndDateDialog) {
                val datePickerState = rememberDatePickerState()
                val confirmEnabled = remember { derivedStateOf { datePickerState.selectedDateMillis != null } }

                DatePickerDialog(
                    onDismissRequest = { openEndDateDialog = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (datePickerState.selectedDateMillis != null) {
                                    val selectedDate = Date(datePickerState.selectedDateMillis!!)
                                    if (selectedDate.before(startDate)) {
                                        coroutineScope.launch {
                                            snackState.showSnackbar("Data końca nie może być wcześniejsza niż data rozpoczęcia!")
                                        }
                                    } else {
                                        endDate = selectedDate
                                    }
                                }
                                openEndDateDialog = false
                            },
                            enabled = confirmEnabled.value
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { openEndDateDialog = false }) {
                            Text("Anuluj")
                        }
                    }
                ) {
                    DatePicker(
                        state = datePickerState,
                        modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())
                    )
                }
            }
        }

        TextButton(onClick = {
            coroutineScope.launch {
                if (selectedMedication != null) {
                    val success = viewModel.createUserMedication(
                        CreateUserMedicationForm(
                            userId = UUID.fromString(removeQuotes(decoded.getClaim("userId").toString())),
                            medicationId = selectedMedication!!.id,
                            dosage = dose,
                            frequency = frequency,
                            startDate = startDate,
                            endDate = if (checked) endDate else null,
                            notes = note
                        )
                    )
                    if (success) {
                        navController.navigate("user_medication_screen")
                    } else {
                        snackState.showSnackbar("Nie udało się dodać leku.")
                    }
                } else {
                    snackState.showSnackbar("Wybierz lek z listy!")
                }
            }
        }) {
            Text(text = "Dodaj lek")
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationDropdownExample(
    medications: List<MedicationResult>,
    onMedicationSelected: (MedicationResult) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
                expanded = true
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            label = { Text("Nazwa leku") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            medications
                .filter { it.name.contains(searchText, ignoreCase = true) || searchText.isEmpty() }
                .forEach { medication ->
                    DropdownMenuItem(
                        text = { Text("${medication.name} (${medication.strength})") },
                        onClick = {
                            searchText = medication.name
                            expanded = false
                            onMedicationSelected(medication)
                        }
                    )
                }

            if (medications.none { it.name.contains(searchText, ignoreCase = true) }) {
                DropdownMenuItem(
                    text = { Text("Brak wyników") },
                    onClick = { expanded = false }
                )
            }
        }
    }
}