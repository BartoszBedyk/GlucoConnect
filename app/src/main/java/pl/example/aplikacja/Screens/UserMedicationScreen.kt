package pl.example.aplikacja.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import pl.example.aplikacja.formatDateTimeWithoutTime
import pl.example.aplikacja.removeQuotes
import pl.example.aplikacja.viewModels.UserMedicationScreenViewModel
import pl.example.networkmodule.apiData.UserMedicationResult
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.getToken

@Composable
fun UserMedicationScreen(navController: NavController?) {

    val context = LocalContext.current

    val apiProvider = remember { ApiProvider(context) }
    val decoded: DecodedJWT = JWT.decode(getToken(context))
    val viewModel = UserMedicationScreenViewModel(
        apiProvider, removeQuotes(decoded.getClaim("userId").toString())
    )
    val medications = viewModel.medicationResults.collectAsState(initial = emptyList())

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {


            LazyColumn {
                items(medications.value) { medication ->
                    MedicationItem(medication) { itemId ->
                        navController?.navigate("medication_result/$itemId")
                    }
                    HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }

        FloatingActionButton(
            onClick = {
                navController?.navigate("add_user_medication")
            },
            shape = Shapes().medium,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        ) {
            Icon(Icons.Filled.Add, "Przycisk do dodawania leków")
        }

    }


}

@Composable
fun MedicationItem(medication: UserMedicationResult, onItemClick: (String) -> Unit) {
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
            Box(
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        onItemClick(medication.medicationId.toString())

                    }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(8.dp)
                )
            }
        }

}

@Preview(showBackground = true)
@Composable
fun MedicationScreenPreview() {
    UserMedicationScreen(NavController(LocalContext.current))
}