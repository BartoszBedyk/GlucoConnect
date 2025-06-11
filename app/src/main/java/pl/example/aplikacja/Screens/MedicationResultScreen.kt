package pl.example.aplikacja.Screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import kotlinx.coroutines.launch
import pl.example.aplikacja.formatDateTimeWithoutTime
import pl.example.aplikacja.removeQuotes
import pl.example.aplikacja.viewModels.MedicationDetailsScreenViewModel
import pl.example.networkmodule.apiData.UserMedicationResult
import pl.example.networkmodule.getToken

@Composable
fun MedicationResultScreen(itemId: String, navController: NavController) {
    val context = LocalContext.current
    val decoded: DecodedJWT = JWT.decode(getToken(context))

    val viewModel = remember {
        MedicationDetailsScreenViewModel(
            context = context,
            removeQuotes(decoded.getClaim("userId").toString()),
            itemId
        )
    }
    val viewModelScope = remember { viewModel.viewModelScope }

    //fetch data from server about medications for actual user

    val userMedication by viewModel.userMedication.collectAsState()
    val medication by viewModel.medication.collectAsState()

    Column(Modifier.fillMaxWidth()) {
        Card {
            userMedication?.let {
                MedicationItem(it)
            }
            Column(Modifier.padding(16.dp)) {
                HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                TextRow(label = "Nazwa leku:", value = medication?.name ?: "")
                TextRow(label = "Opis:", value = medication?.description ?: "")
                TextRow(label = "Producent:", value = medication?.manufacturer ?: "")
                TextRow(label = "Forma:", value = medication?.form ?: "")
                TextRow(label = "Siła:", value = medication?.strength ?: "")
            }
            FloatingActionButton( onClick = {
                viewModelScope.launch {
                    if(viewModel.deleteUserMedicationById()){
                        Log.d("GlucoseDetails", "Glucose result deleted successfully")
                        Toast.makeText(context, "Usunięto lek!", Toast.LENGTH_LONG).show()
                        navController.popBackStack()
                    }
                    else{
                        Toast.makeText(context, "Nie udało się usunąć leku!", Toast.LENGTH_LONG).show()
                        navController.popBackStack()
                    }
                }},
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.End)
            ){
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}



@Composable
fun MedicationItem(medication: UserMedicationResult) {
    Row(
        modifier = Modifier.padding(16.dp)
    ) {

        Column {
            Text(
                text = "Nazwa leku: ${medication.medicationName}",
                style = MaterialTheme.typography.labelLarge
            )
            Text(text = "Dawka: ${medication.dosage}")
            Text(text = "Częstotliwość: ${medication.frequency}")
            Text(text = "Przepisany od: ${formatDateTimeWithoutTime(medication.startDate)}")
            medication.endDate?.let {
                Text(text = "Przepisany do: ${formatDateTimeWithoutTime(it)}")
            }
        }
    }

}

@Preview
@Composable
fun MedicationResultScreenPreview() {
    //MedicationResultScreen("1")
}