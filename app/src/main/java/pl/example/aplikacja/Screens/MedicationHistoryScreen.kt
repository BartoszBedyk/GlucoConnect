package pl.example.aplikacja.Screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import pl.example.aplikacja.mappters.formatDateTimeWithoutTime
import pl.example.aplikacja.viewModels.MedicationHistoryViewModel
import pl.example.networkmodule.apiData.UserMedicationResult

@Composable
fun MedicationHistoryScreen(
    navController : NavController
){

    val viewModel : MedicationHistoryViewModel = hiltViewModel()
    val medicationResults = viewModel.medicationResults.collectAsState()


        Column(Modifier.fillMaxSize()) {


            LazyColumn {
                items(medicationResults.value) { medication ->
                    MedicationItemHistory(medication)
                    HorizontalDivider(
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }



}

@Composable
fun MedicationItemHistory(medication: UserMedicationResult) {
    var isExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable {
                isExpanded = !isExpanded
            }) {
        Log.d("MedicationItem", "medication: $medication")

        Column(Modifier
        ) {
            Text(
                text = "Nazwa leku: ${medication.medicationName}",
                style = MaterialTheme.typography.labelLarge
            )
            Text(text = "Przepisany od: ${formatDateTimeWithoutTime(medication.startDate)}")
            Text(text = "Przepisany do: ${formatDateTimeWithoutTime(medication.endDate)}")
            AnimatedVisibility(
                visible = isExpanded, modifier = Modifier
                    .padding(end = 0.dp)
            ){
                Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(text = "Dawka: ${medication.dosage}")
                    Text(text = "Częstotliwość: ${medication.frequency}")
                    Text(text = "${medication.notes}")
                }
            }
        }
    }
}