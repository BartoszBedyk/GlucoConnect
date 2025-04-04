package pl.example.aplikacja.Screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import kotlinx.coroutines.launch
import pl.example.aplikacja.formatDateTimeSpecificLocale
import pl.example.aplikacja.formatUnit
import pl.example.aplikacja.removeQuotes
import pl.example.aplikacja.viewModels.GlucoseDetailsScreenViewModel
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.getToken

@Composable
fun GlucoseResultScreen(id: String, navController: NavController) {
    val context = LocalContext.current
    val decoded: DecodedJWT = JWT.decode(getToken(context))
    val viewModel = remember {
        GlucoseDetailsScreenViewModel(
            context,
            id,
            removeQuotes(decoded.getClaim("userId").toString())
        )
    }

    val researchResult by viewModel.glucoseResult.collectAsState()
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
                    text = "Dane pomiaru glukozy",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                //TextRow(label = "ID pomiaru", value = researchResult?.id.toString())
                //TextRow(label = "Sequence Number", value = researchResult?.sequenceNumber.toString())
                TextRow(
                    label = "Stężenie glukozy",
                    value = "${researchResult?.glucoseConcentration} ${
                        researchResult?.unit?.let {
                            formatUnit(
                                it
                            )
                        }
                    }"
                )
                TextRow(
                    label = "Czas Pomiaru",
                    value = researchResult?.timestamp?.let { formatDateTimeSpecificLocale(it) }
                        ?: "Brak danych"
                )
                TextRow(
                    label = "Ostatnia edycja",
                    value = researchResult?.lastUpdatedOn?.let { formatDateTimeSpecificLocale(it) }
                        ?: "Nie edytowano"
                )
//                TextRow(
//                    label = "Skasowano",
//                    value = researchResult?.deletedOn?.let { formatDateTimeSpecificLocale(it) }
//                        ?: "Nie skasowano"
//                )
//                TextRow(
//                    label = "Użytkownik",
//                    value = researchResult?.userId?.toString() ?: "Brak danych"
//                )

                FloatingActionButton(
                    onClick = {
                        viewModelScope.launch {
                            if (viewModel.deleteGlucoseResult()) {
                                Log.d("GlucoseDetails", "Glucose result deleted successfully")
                                Toast.makeText(context, "Pomiar usunięty!", Toast.LENGTH_LONG)
                                    .show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Nie udało się usunąć pomiaru!",
                                    Toast.LENGTH_LONG
                                ).show()
                                navController.popBackStack()
                            }
                        }
                    },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.End)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                    //Text(text = "Usuń")
                }

                if (researchResult != null) {
                    Column {
                        Text(
                            text = "Analiza dla stanu na czczo",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            evaluateGlucoseResultNoFood(
                                researchResult!!.unit,
                                researchResult!!.glucoseConcentration
                            )
                        )
                        Text(
                            text = "Analiza dla stanu po posiłku",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            evaluateGlucoseResultAfterMeal(
                                researchResult!!.unit,
                                researchResult!!.glucoseConcentration
                            )
                        )
                    }
                }
            }


        }
    }
}

@Composable
fun evaluateGlucoseResultNoFood(unit: GlucoseUnitType, glucoseConcentration: Double): String {
    return when (unit) {
        GlucoseUnitType.MG_PER_DL -> {
            when {
                glucoseConcentration < 70 -> "Poziom glukozy poniżej normy (hipoglikemia)"
                glucoseConcentration in 70.0..99.9 -> "Prawidłowy poziom glukozy na czczo"
                glucoseConcentration in 100.0..125.9 -> "Podwyższony poziom glukozy – stan przedcukrzycowy"
                glucoseConcentration >= 126 -> "Poziom glukozy wskazuje na cukrzycę (jeśli pomiar był na czczo)"
                else -> "Nieprawidłowa wartość pomiaru"
            }
        }

        GlucoseUnitType.MMOL_PER_L -> {
            when {
                glucoseConcentration < 3.9 -> "Poziom glukozy poniżej normy (hipoglikemia)"
                glucoseConcentration in 3.9..5.5 -> "Prawidłowy poziom glukozy na czczo"
                glucoseConcentration in 5.6..6.9 -> "Podwyższony poziom glukozy – stan przedcukrzycowy"
                glucoseConcentration >= 7.0 -> "Poziom glukozy wskazuje na cukrzycę (jeśli pomiar był na czczo)"
                else -> "Nieprawidłowa wartość pomiaru"
            }
        }
    }
}

fun evaluateGlucoseResultAfterMeal(unit: GlucoseUnitType, glucoseConcentration: Double): String {
    return when (unit) {
        GlucoseUnitType.MG_PER_DL -> {
            when {
                glucoseConcentration < 140 -> "Prawidłowy poziom glukozy po posiłku"
                glucoseConcentration in 140.0..199.9 -> "Podwyższony poziom glukozy – stan przedcukrzycowy"
                glucoseConcentration >= 200 -> "Poziom glukozy wskazuje na cukrzycę"
                else -> "Nieprawidłowa wartość pomiaru"
            }
        }

        GlucoseUnitType.MMOL_PER_L -> {
            when {
                glucoseConcentration < 7.8 -> "Prawidłowy poziom glukozy po posiłku"
                glucoseConcentration in 7.8..10.9 -> "Podwyższony poziom glukozy – stan przedcukrzycowy"
                glucoseConcentration >= 11.1 -> "Poziom glukozy wskazuje na cukrzycę"
                else -> "Nieprawidłowa wartość pomiaru"
            }
        }
    }
}


@Composable
fun TextRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}





