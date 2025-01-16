package pl.example.aplikacja.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import kotlinx.coroutines.launch
import pl.example.aplikacja.formatDateTimeWithoutLocale
import pl.example.aplikacja.removeQuotes
import pl.example.aplikacja.viewModels.AddHeartbeatViewModel
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.getToken
import pl.example.networkmodule.requestData.CreateHeartbeatForm
import java.util.Calendar
import java.util.Date
import java.util.UUID

@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHeartbeatResultScreen(navController: NavHostController) {

    val systolicPressure by remember { mutableIntStateOf(0) }
    val diastolicPressure by remember { mutableIntStateOf(0) }
    val pulse by remember { mutableIntStateOf(0) }
    val note by remember { mutableStateOf("") }

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
    val apiProvider = remember { ApiProvider(context) }
    val decoded: DecodedJWT = JWT.decode(getToken(context))
    val viewModel =
        AddHeartbeatViewModel(apiProvider)

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
            label = "Systolicna ciśnienie",
            value = systolicPressure.toString(),
            fontSize = 20
        )

        TextRowEdit(
            label = "Diastoliczna ciśnienie",
            value = diastolicPressure.toString(),
            fontSize = 20
        )

        TextRowEdit(
            label = "Puls",
            value = pulse.toString(),
            fontSize = 20
        )

        TextRowEdit(
            label = "Notatka",
            value = note,
            fontSize = 20
        )
        Row(){
            Text(text = "Data pomiaru")
            Checkbox(checked = checked, onCheckedChange = { state ->
                if (state) {
                    openDialogDate = true
                    openDateTimePicker = true
                }
                checked = state
            }
            )

        }



        if (checked) {
            TextRowEdit(
                label = "Data pomiaru",
                value = timestampFull?.let { formatDateTimeWithoutLocale(it) }
                    ?: "",
                fontSize = 20
            )

            if (openDialogDate) {
                val datePickerState = rememberDatePickerState()
                val confirmEnabled = remember {
                    derivedStateOf { datePickerState.selectedDateMillis != null }
                }
                DatePickerDialog(onDismissRequest = {
                    openDialogDate = false
                    openDateTimePicker = false
                }, confirmButton = {
                    TextButton(
                        onClick = {
                            if (datePickerState.selectedDateMillis != null) {
                                timestampDate = Date(datePickerState.selectedDateMillis!!)
                            }
                            openDialogDate = false
                            openDateTimePicker = false
                            openClockTimePicker = true
                        }, enabled = confirmEnabled.value
                    ) {
                        Text("OK")
                    }
                }, dismissButton = {
                    TextButton(onClick = {
                        openDialogDate = false
                        openDateTimePicker = false
                        openClockTimePicker = false
                    }) {
                        Text("Anuluj")
                    }
                }) {
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
        TextButton(onClick = {
            coroutineScope.launch {
                if (
                    viewModel.addHeartbeatResult(
                        CreateHeartbeatForm(
                            userId = UUID.fromString(
                                removeQuotes(
                                    decoded.getClaim("userId").toString()
                                )
                            ),
                            timestamp = timestampFull ?: Date(),
                            pulse = pulse,
                            systolicPressure = systolicPressure,
                            diastolicPressure = diastolicPressure,
                            note = note
                        )
                    )
                ) {
                    navController.navigate("all_results_screen/true")
                } else {
                    snackState.showSnackbar("Nie udało się dodać pomiaru")
                }
            }
        }) {
            Text(text = "Dodaj pomiar")
        }
    }
}