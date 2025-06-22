package pl.example.aplikacja.Screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import pl.example.aplikacja.R
import pl.example.aplikacja.UiElements.UserMedicationSwapItem
import pl.example.aplikacja.mappters.formatDateTimeWithoutTime
import pl.example.aplikacja.mappters.removeQuotes
import pl.example.aplikacja.viewModels.UserMedicationScreenViewModel
import pl.example.networkmodule.apiData.UserMedicationResult
import pl.example.networkmodule.getToken

@Composable
fun UserMedicationScreen(navController: NavController?) {

    val context = LocalContext.current


    val decoded: DecodedJWT = JWT.decode(getToken(context))
    val viewModel = remember {
        UserMedicationScreenViewModel(
            context, removeQuotes(decoded.getClaim("userId").toString())
        )
    }
    //download medications for specyfic time
    val medications = viewModel.medicationResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Box(Modifier.fillMaxSize()) {
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

            Column(Modifier.fillMaxSize()) {


                LazyColumn {
                    items(medications.value) { medication ->
                        UserMedicationSwapItem(medication, modifier = Modifier, {},{}, { itemId -> navController?.navigate("medication_result/$itemId")})
                        HorizontalDivider(
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }

            if(isNetworkAvailable(context)) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.fillMaxSize()
                    ) {



                        FloatingActionButton(
                            onClick = {
                                navController?.navigate("add_user_medication_screen")
                            },
                            shape = Shapes().medium,
                            modifier = Modifier
                                .padding(vertical = 8.dp, horizontal = 16.dp),
                            elevation = FloatingActionButtonDefaults.elevation(4.dp)
                        ) {
                            Icon(Icons.Filled.Add, "Przycisk do dodawania leków")
                        }
                        FloatingActionButton(
                            onClick = {
                                navController?.navigate("medication_history_screen")
                            },
                            shape = Shapes().medium,
                            modifier = Modifier
                                .padding(vertical = 8.dp, horizontal = 16.dp),
                            elevation = FloatingActionButtonDefaults.elevation(4.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_history_24),
                                contentDescription = "Przycisk do historii"
                            )
                        }
                    }
                }
            }

        }
    }


}

@Composable
fun MedicationItem(medication: UserMedicationResult, onItemClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable {
            onItemClick(medication.medicationId.toString())
        }
    ) {
        Log.d("MedicationItem", "medication: $medication")

        Column() {
            Text(
                text = "Nazwa leku: ${medication.medicationName}",
                style = MaterialTheme.typography.labelLarge
            )
            Text(text = "Dawka: ${medication.dosage}")
            Text(text = "Częstotliwość: ${medication.frequency}")
            Text(text = "Przepisany od: ${formatDateTimeWithoutTime(medication.startDate)}")
            Text(text = "Przepisany do: ${formatDateTimeWithoutTime(medication.endDate)}")
        }


        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
//            Icon(
//                imageVector = Icons.Default.Info,
//                contentDescription = null,
//                modifier = Modifier
//                    .padding(8.dp)
//                    .clickable {
//
//                    }
//            )
//            Icon(
//                imageVector = Icons.Default.Lock,
//                contentDescription = null,
//                modifier = Modifier
//                    .padding(8.dp)
//                    .clickable {
//
//                    }
//            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MedicationScreenPreview() {
    UserMedicationScreen(NavController(LocalContext.current))
}