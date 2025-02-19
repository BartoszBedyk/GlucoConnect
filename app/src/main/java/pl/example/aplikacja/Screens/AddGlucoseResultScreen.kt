@file:Suppress("DEPRECATION")

package pl.example.aplikacja.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import kotlinx.coroutines.launch
import pl.example.aplikacja.UiElements.GlucoseUnitDropdownMenu
import pl.example.aplikacja.formatDateTimeWithoutLocale
import pl.example.aplikacja.removeQuotes
import pl.example.aplikacja.viewModels.AddGlucoseResultViewModel
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.getToken
import pl.example.networkmodule.requestData.ResearchResultCreate
import java.util.Calendar
import java.util.Date
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGlucoseResultScreen(navController: NavHostController, fromMain: Boolean? = false) {
    val glucoseConcentrationState = remember { mutableStateOf("0.0") }
    var unitState by remember { mutableStateOf<GlucoseUnitType?>(null) }
    var timestampDate by remember { mutableStateOf<Date?>(null) }
    var timestampTime by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var checked by remember { mutableStateOf(false) }
    var openDialogDate by remember { mutableStateOf(false) }
    var openDateTimePicker by remember { mutableStateOf(false) }
    var openClockTimePicker by remember { mutableStateOf(false) }
    val snackState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    val decoded: DecodedJWT = JWT.decode(getToken(context))
    val viewModel = remember { AddGlucoseResultViewModel(context, removeQuotes(decoded.getClaim("userId").toString())) }


    val prefUnit by viewModel.prefUnit.collectAsState()


    LaunchedEffect(prefUnit) {
        if (unitState == null) {
            unitState = prefUnit
        }
    }

    val timestampFull by remember {
        derivedStateOf {
            if (timestampDate != null && timestampTime != null) {
                Date(timestampDate!!.time).apply {
                    hours = timestampTime!!.first
                    minutes = timestampTime!!.second
                }
            } else null
        }
    }

    SnackbarHost(hostState = snackState, Modifier)

    Column(Modifier.padding(16.dp)) {
        TextRowEdit(
            label = "Poziom glukozy",
            value = glucoseConcentrationState.value,
            onValueChange = { glucoseConcentrationState.value = it },
            fontSize = 20
        )

        Text(
            text = "Jednostka stężenia glukozy",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 15.sp
        )

        unitState?.let {
            GlucoseUnitDropdownMenu(
                selectedUnit = it,
                onUnitSelected = { unitState = it },
                label = ""
            )
        }

        Row(verticalAlignment = CenterVertically) {
            Text(text = "Data pomiaru")
            Checkbox(checked = checked, onCheckedChange = { state ->
                if (state) {
                    openDialogDate = true
                    openDateTimePicker = true
                }
                checked = state
            })
        }

        if (checked) {
            TextRowEdit(
                label = "Data pomiaru",
                value = timestampFull?.let { formatDateTimeWithoutLocale(it) } ?: "",
                onValueChange = {}, // Pole tylko do odczytu
                fontSize = 20
            )

            if (openDialogDate) {
                val datePickerState = rememberDatePickerState()
                val confirmEnabled = remember {
                    derivedStateOf { datePickerState.selectedDateMillis != null }
                }
                DatePickerDialog(
                    onDismissRequest = {
                        openDialogDate = false
                        openDateTimePicker = false
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (datePickerState.selectedDateMillis != null) {
                                    timestampDate = Date(datePickerState.selectedDateMillis!!)
                                }
                                openDialogDate = false
                                openDateTimePicker = false
                                openClockTimePicker = true
                            },
                            enabled = confirmEnabled.value
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            openDialogDate = false
                            openDateTimePicker = false
                            openClockTimePicker = false
                        }) {
                            Text("Anuluj")
                        }
                    }
                ) {
                    DatePicker(
                        state = datePickerState,
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    )
                }
            }
            if (openClockTimePicker) {
                CustomTimePicker(
                    timePickerState = timePickerState,
                    onDismiss = {
                        openClockTimePicker = false
                    },
                    onConfirm = {
                        timestampTime = Pair(timePickerState.hour, timePickerState.minute)
                        openClockTimePicker = false
                    }
                )
            }
        }

        FloatingActionButton(
            onClick = {
                navController.navigate("bluetooth_permission_screen/addResult")
            },
            modifier = Modifier
                .padding(16.dp)
                .align(androidx.compose.ui.Alignment.CenterHorizontally)
        ) {
            Text(text = "Użyj glukometru")
        }

        TextButton(onClick = {
            coroutineScope.launch {
                if (viewModel.addGlucoseResult(
                        ResearchResultCreate(
                            userId = UUID.fromString(
                                removeQuotes(
                                    decoded.getClaim("userId").toString()
                                )
                            ),
                            sequenceNumber = 1,
                            glucoseConcentration = glucoseConcentrationState.value.toDoubleOrNull()
                                ?: 0.0,
                            unit = unitState.toString(),
                            timestamp = timestampFull ?: Date()
                        )
                    )
                ) {
                    if (fromMain == true) {
                        navController.navigate("main_screen")
                    } else {
                        navController.navigate("all_results_screen/false")
                    }
                } else {
                    snackState.showSnackbar("Nie udało się dodać pomiaru")
                }
            }
        }) {
            Text(text = "Dodaj pomiar")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTimePicker(
    timePickerState: TimePickerState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {

    Dialog(onDismissRequest = onDismiss) {
        Card(Modifier.padding(16.dp)) {
            TimePicker(
                state = timePickerState,
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            )
            TextButton(onClick = onConfirm) {
                Text("OK")
            }
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        }
    }
}


@Preview
@Composable
fun AddGlucoseResultScreenPreview() {
    AddGlucoseResultScreen(NavHostController(LocalContext.current))
}





