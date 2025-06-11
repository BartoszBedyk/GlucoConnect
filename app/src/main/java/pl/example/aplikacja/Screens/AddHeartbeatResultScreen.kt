package pl.example.aplikacja.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import kotlinx.coroutines.launch
import pl.example.aplikacja.formatDateTimeWithoutLocale
import pl.example.aplikacja.removeQuotes
import pl.example.aplikacja.viewModels.AddHeartbeatViewModel
import pl.example.networkmodule.getToken
import pl.example.networkmodule.requestData.CreateHeartbeatForm
import java.util.Calendar
import java.util.Date
import java.util.UUID

@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHeartbeatResultScreen(navController: NavHostController, fromMain: Boolean? = false) {

    //form data variables
    var systolicPressure by remember { mutableStateOf("0") }
    var diastolicPressure by remember { mutableStateOf("0") }
    var pulse by remember { mutableStateOf("0") }
    var note by remember { mutableStateOf("") }

    var timestampDate by remember { mutableStateOf<Date?>(null) }
    var timestampTime by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var timeDateCheckbox by remember { mutableStateOf(false) }
    var openDialogDate by remember { mutableStateOf(false) }
    var openDateTimePicker by remember { mutableStateOf(false) }
    var openClockTimePicker by remember { mutableStateOf(false) }


    val snackState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    val viewModel : AddHeartbeatViewModel = hiltViewModel()

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

    Column(
        Modifier.padding(18.dp),
    ) {
        OutlinedCard() {
            Column(Modifier.padding(16.dp)) {
                TextRowEdit(
                    label = "Systoliczne ciśnienie",
                    value = systolicPressure,
                    onValueChange = { systolicPressure = it },
                    fontSize = 18,
                    true
                )

                TextRowEdit(
                    label = "Diastoliczne ciśnienie",
                    value = diastolicPressure,
                    onValueChange = { diastolicPressure = it },
                    fontSize = 18,
                    true
                )

                TextRowEdit(
                    label = "Puls",
                    value = pulse,
                    onValueChange = { pulse = it },
                    fontSize = 18,
                    true
                )

                TextRowEdit(
                    label = "Notatka",
                    value = note,
                    onValueChange = { note = it },
                    fontSize = 18,
                    false
                )

                Row(verticalAlignment = CenterVertically) {
                    Text(
                        text = "Data pomiaru",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp
                    )
                    Checkbox(checked = timeDateCheckbox, onCheckedChange = { state ->
                        if (state) {
                            openDialogDate = true
                            openDateTimePicker = true
                        }
                        timeDateCheckbox = state
                    })
                }

                if (timeDateCheckbox) {
                    TextRowEdit(
                        label = "Data pomiaru",
                        value = timestampFull?.let { formatDateTimeWithoutLocale(it) } ?: "",
                        onValueChange = {},
                        fontSize = 18,
                        true
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
                                            timestampDate =
                                                Date(datePickerState.selectedDateMillis!!)
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

                Row(verticalAlignment = CenterVertically) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                    ) {
                        ExtendedFloatingActionButton(
                            onClick = {
                                coroutineScope.launch {
                                    if (
                                        viewModel.addHeartbeatResult(
                                            CreateHeartbeatForm(
                                                userId = UUID.fromString(
                                                    viewModel.USER_ID
                                                ),
                                                timestamp = timestampFull ?: Date(),
                                                pulse = pulse.toIntOrNull() ?: 0,
                                                systolicPressure = systolicPressure.toIntOrNull()
                                                    ?: 0,
                                                diastolicPressure = diastolicPressure.toIntOrNull()
                                                    ?: 0,
                                                note = note
                                            )
                                        )
                                    ) {
                                        if (fromMain == true) {
                                            navController.navigate("main_screen")
                                        } else {
                                            navController.navigate("all_results_screen/true")
                                        }
                                    } else {
                                        snackState.showSnackbar("Nie udało się dodać pomiaru")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Dodaj pomiar")
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AddHeartbeatResultScreenPreview() {
    AddHeartbeatResultScreen(NavHostController(LocalContext.current))
}