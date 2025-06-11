package pl.example.aplikacja.Screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import pl.example.aplikacja.formatDateTimeWithoutTime
import pl.example.aplikacja.viewModels.AddUserMedicationViewModel
import pl.example.networkmodule.apiData.MedicationResult
import pl.example.networkmodule.requestData.CreateUserMedicationForm
import java.util.Date
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserMedicationScreen(navController: NavController) {

    //Form data variables
    var dose by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<Date?>(null) }
    var endDate by remember { mutableStateOf<Date?>(null) }
    var openStartDateDialog by remember { mutableStateOf(false) }
    var openEndDateDialog by remember { mutableStateOf(false) }


    val snackState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current



    val viewModel : AddUserMedicationViewModel = hiltViewModel()

    val medication = viewModel.medications.collectAsState()
    var selectedMedication by remember { mutableStateOf<MedicationResult?>(null) }

    val startDatePickerState = rememberDatePickerState(initialSelectedDateMillis = startDate?.time)
    val endDatePickerState = rememberDatePickerState(initialSelectedDateMillis = endDate?.time)

    SnackbarHost(hostState = snackState, Modifier)

    Column(
        Modifier.padding(18.dp),
    ) {
        OutlinedCard() {
            Column(Modifier.padding(16.dp)) {

                MedicationDropdownExample(medications = medication.value,
                    onMedicationSelected = { selectedMedication = it })

                TextRowEdit(
                    label = "Dawka",
                    value = dose,
                    onValueChange = { dose = it },
                    fontSize = 18,
                    false
                )

                TextRowEdit(
                    label = "Częstotliwość",
                    value = frequency,
                    onValueChange = { frequency = it },
                    fontSize = 18,
                    false
                )

                TextRowEdit(
                    label = "Notatka",
                    value = note,
                    onValueChange = { note = it },
                    fontSize = 18,
                    false
                )

                Row {
                    Text(
                        text = "Data początku",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp,
                        modifier = Modifier.align(CenterVertically)
                    )
                    Checkbox(
                        checked = startDate != null, onCheckedChange = { state ->
                            if (state) {
                                openStartDateDialog = true
                            } else {
                                startDate = null
                                Log.d("AddUserMedication", "startDate set to null")
                            }
                        }, modifier = Modifier.align(CenterVertically)
                    )
                }

                startDate?.let {
                    TextRowEdit(
                        label = "Data początku:",
                        value = formatDateTimeWithoutTime(it),
                        onValueChange = {},
                        fontSize = 18,
                        false
                    )
                }

                if (openStartDateDialog) {
                    DatePickerDialog(onDismissRequest = { openStartDateDialog = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    startDatePickerState.selectedDateMillis?.let {
                                        startDate = Date(it)
                                        Log.d("AddUserMedication", "startDate set to: $startDate")
                                    }
                                    openStartDateDialog = false
                                }, enabled = startDatePickerState.selectedDateMillis != null
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { openStartDateDialog = false }) {
                                Text("Anuluj")
                            }
                        }) {
                        DatePicker(state = startDatePickerState, modifier = Modifier.padding(16.dp))
                    }
                }

                Row {
                    Text(
                        text = "Data końca",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp,
                        modifier = Modifier.align(CenterVertically)
                    )
                    Checkbox(
                        checked = endDate != null, onCheckedChange = { state ->
                            if (state) {
                                openEndDateDialog = true
                            } else {
                                endDate = null
                                Log.d("AddUserMedication", "endDate set to null")
                            }
                        }, modifier = Modifier.align(CenterVertically)
                    )
                }

                endDate?.let {
                    TextRowEdit(
                        label = "Data zakończenia:",
                        value = formatDateTimeWithoutTime(it),
                        onValueChange = {},
                        fontSize = 18,
                        false
                    )
                }

                if (openEndDateDialog) {
                    DatePickerDialog(onDismissRequest = { openEndDateDialog = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    endDatePickerState.selectedDateMillis?.let {
                                        val selectedDate = Date(it)
                                        if (startDate != null && selectedDate.before(startDate)) {
                                            coroutineScope.launch {
                                                snackState.showSnackbar("Data końca nie może być wcześniejsza niż data początku!")
                                            }
                                        } else {
                                            endDate = selectedDate
                                            Log.d("AddUserMedication", "endDate set to: $endDate")
                                        }
                                    }
                                    openEndDateDialog = false
                                }, enabled = endDatePickerState.selectedDateMillis != null
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { openEndDateDialog = false }) {
                                Text("Anuluj")
                            }
                        }) {
                        DatePicker(state = endDatePickerState, modifier = Modifier.padding(16.dp))
                    }
                }

                ExtendedFloatingActionButton(onClick = {
                    coroutineScope.launch {
                        if (selectedMedication != null) {
                            if (startDate != null && endDate != null && endDate!!.before(startDate)) {
                                snackState.showSnackbar("Data końca nie może być wcześniejsza niż data początku!")
                                return@launch
                            }
                            Log.d(
                                "AddUserMedication",
                                "Submitting with startDate: $startDate, endDate: $endDate"
                            )
                            if (viewModel.addUserMedication(
                                    CreateUserMedicationForm(
                                        userId = UUID.fromString(
                                            viewModel.USER_ID
                                        ),
                                        medicationId = selectedMedication!!.id,
                                        dosage = dose,
                                        frequency = frequency,
                                        startDate = startDate,
                                        endDate = endDate,
                                        notes = note
                                    )
                                )
                            ) {
                                navController.navigate("user_medication_screen")
                                snackState.showSnackbar("Dodano lek.")
                            } else {
                                snackState.showSnackbar("Nie udało się dodać leku.")
                            }
                        } else {
                            snackState.showSnackbar("Wybierz lek z listy!")
                        }
                    }
                },modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()) {
                    Text(text = "Dodaj lek")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationDropdownExample(
    medications: List<MedicationResult>, onMedicationSelected: (MedicationResult) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var filteredMedications by remember { mutableStateOf(medications) }

    fun filterMedications(query: String) {
        filteredMedications = medications.filter {
            it.name.contains(query, ignoreCase = true) || query.isEmpty()
        }
    }

    LaunchedEffect(searchText) {
        kotlinx.coroutines.delay(300)
        filterMedications(searchText)
        expanded = searchText.isNotEmpty()
    }

    Text(
        text = "Nazwa leku",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 18.sp
    )
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            label = { Text("Nazwa leku") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            singleLine = true
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            if (filteredMedications.isEmpty()) {
                DropdownMenuItem(text = { Text("Brak wyników") }, onClick = { expanded = false })
            } else {
                filteredMedications.forEach { medication ->
                    DropdownMenuItem(text = { Text("${medication.name} (${medication.strength})") },
                        onClick = {
                            searchText = medication.name
                            expanded = false
                            onMedicationSelected(medication)
                        })
                }
            }
        }
    }
}


@Preview
@Composable
fun AddUserMedicationScreenPreview() {
    AddUserMedicationScreen(NavHostController(LocalContext.current))
}