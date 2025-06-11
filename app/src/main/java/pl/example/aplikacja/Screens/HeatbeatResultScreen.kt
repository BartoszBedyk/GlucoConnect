package pl.example.aplikacja.Screens



import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import pl.example.aplikacja.formatDateTimeSpecificLocale
import pl.example.aplikacja.viewModels.HeartbeatDetailsScreenViewModel

@Composable
fun HeartbeatResultScreen(id: String, navController: NavController) {
    val context = LocalContext.current
    val viewModel =
        remember {HeartbeatDetailsScreenViewModel(context, id)}


    //fetch data from server about heartbeats for actual user
    val heartbeatResult by viewModel.heartbeatResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val viewModelScope = remember { viewModel.viewModelScope }
    if (isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                Text(
                    text = "Nawiązywanie połączenia...",
                    modifier = Modifier.padding(16.dp),
                    color = androidx.compose.ui.graphics.Color.Gray
                )
            }
        }
    } else {
        Column {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Dane pomiaru tętna",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    //TextRow(label = "ID pomiaru", value = heartbeatResult?.id.toString())
                    TextRow(label = "Czas Pomiaru", value = heartbeatResult?.timestamp?.let {
                        formatDateTimeSpecificLocale(it)
                    } ?: "Brak danych")
                    TextRow(
                        label = "Ciśnienie skurczowe",
                        value = "${heartbeatResult?.systolicPressure} mmHg"
                    )
                    TextRow(
                        label = "Ciśnienie rozkurczowe",
                        value = "${heartbeatResult?.diastolicPressure} mmHg"
                    )
                    TextRow(label = "Puls", value = "${heartbeatResult?.pulse} bpm")
                    if(heartbeatResult?.note?.isNotBlank() == true){
                            TextRow(label = "Notatka", value = heartbeatResult?.note ?: "Brak danych")
                        }


                    FloatingActionButton(
                        onClick = {
                            viewModelScope.launch {
                            if (viewModel.deleteHeartbeatResult()) {
                                navController.popBackStack()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Nie udało się usunąć pomiaru!",
                                    Toast.LENGTH_LONG
                                ).show()
                                navController.popBackStack()
                            }
                        }},
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .align(Alignment.End)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                        //Text(text = "Usuń")
                    }
                }


            }
            if (heartbeatResult != null) {
                Text(
                    evaluateBloodPressure(
                        heartbeatResult!!.systolicPressure,
                        heartbeatResult!!.diastolicPressure,
                        heartbeatResult!!.pulse
                    ),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

//Funtion for evaluation of medical data (blood pressure)

fun evaluateBloodPressure(
    systolicPressure: Int,
    diastolicPressure: Int,
    pulse: Int
): String {
    val bloodPressureMessage = when {
        systolicPressure < 90 && diastolicPressure < 60 ->
            "Ciśnienie tętnicze jest za niskie (niedociśnienie)."

        systolicPressure in 90..119 && diastolicPressure in 60..79 ->
            "Ciśnienie tętnicze jest w normie (optymalne)."

        systolicPressure in 120..129 && diastolicPressure in 80..84 ->
            "Ciśnienie tętnicze jest prawidłowe, ale zbliża się do górnych granic normy."

        systolicPressure in 130..139 || diastolicPressure in 85..89 ->
            "Ciśnienie tętnicze jest wysokie prawidłowe."

        systolicPressure in 140..159 || diastolicPressure in 90..99 ->
            "Ciśnienie tętnicze wskazuje na nadciśnienie stopnia 1."

        systolicPressure in 160..179 || diastolicPressure in 100..109 ->
            "Ciśnienie tętnicze wskazuje na nadciśnienie stopnia 2."

        systolicPressure >= 180 || diastolicPressure >= 110 ->
            "Ciśnienie tętnicze wskazuje na nadciśnienie stopnia 3. Skonsultuj się z lekarzem!"

        else -> "Nieprawidłowe dane pomiarowe."
    }

    val conditionsMessage = "Upewnij się, że pomiar został wykonany w spoczynku, w pozycji siedzącej, po co najmniej 5 minutach odpoczynku."

    val pulseMessage = when {
        pulse < 60 -> "Tętno jest zbyt niskie (bradykardia)."
        pulse in 60..100 -> "Tętno jest w normie."
        pulse > 100 -> "Tętno jest zbyt wysokie (tachykardia)."
        else -> "Nieprawidłowy wynik tętna."
    }

    return "$bloodPressureMessage $pulseMessage $conditionsMessage"
}


