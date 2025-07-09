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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import kotlinx.coroutines.launch
import pl.example.aplikacja.mappters.formatDateTimeSpecificLocale
import pl.example.aplikacja.mappters.formatUnit
import pl.example.aplikacja.mappters.removeQuotes
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

    //fetch data from server about glucose for actual user
    val researchResult by viewModel.glucoseResult.collectAsState()
    val diabetesType by viewModel.diabetesType.collectAsState()
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
                            text = "Analiza pomiaru",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            evaluateGlucoseResult(
                                researchResult!!.unit,
                                researchResult!!.glucoseConcentration,
                                researchResult!!.afterMedication,
                                researchResult!!.emptyStomach
                            )
                        )

                        HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

                        Text(
                            text = "Analiza pomiaru z uwzględnieniem cukrzycy",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            evaluateGlucoseWithDiabetesType(
                                researchResult!!.unit,
                                researchResult!!.glucoseConcentration,
                                researchResult!!.afterMedication,
                                researchResult!!.emptyStomach,
                                diabetesType!!
                            )
                        )



                    }
                }
            }


        }
    }
}

@Composable
fun evaluateGlucoseResult(
    unit: GlucoseUnitType,
    glucoseConcentration: Double,
    afterMedication: Boolean,
    emptyStomach: Boolean
): String {
    val concentrationMgDl = when (unit) {
        GlucoseUnitType.MG_PER_DL -> glucoseConcentration
        GlucoseUnitType.MMOL_PER_L -> glucoseConcentration * 18.0182
    }

    return if (emptyStomach) {
        // Ocena na czczo
        when {
            concentrationMgDl < 70 -> "Hipoglikemia (poziom glukozy poniżej normy)"
            concentrationMgDl in 70.0..99.9 -> "Prawidłowy poziom glukozy na czczo"
            concentrationMgDl in 100.0..125.9 -> "Stan przedcukrzycowy (nieprawidłowa glikemia na czczo)"
            concentrationMgDl >= 126 -> "Wskazuje na cukrzycę (potwierdź badaniem powtórnym)"
            else -> "Nieprawidłowa wartość pomiaru"
        }
    } else {
        // Ocena po posiłku (2h po)
        when {
            concentrationMgDl < 140 -> "Prawidłowy poziom glukozy po posiłku"
            concentrationMgDl in 140.0..199.9 -> "Stan przedcukrzycowy (nieprawidłowa glikemia poposiłkowa)"
            concentrationMgDl >= 200 -> "Wskazuje na cukrzycę (potwierdź badaniem powtórnym)"
            else -> "Nieprawidłowa wartość pomiaru"
        }
    }.let { baseMessage ->
        val medsInfo = if (afterMedication) {
            "Uwaga: pomiar wykonany po zażyciu leków obniżających glukozę, interpretuj ostrożnie."
        } else {
            ""
        }
        listOf(baseMessage, medsInfo).filter { it.isNotBlank() }.joinToString(" ")
    }
}

enum class DiabetesType {
    TYPE_1, TYPE_2, MODY, LADA, GESTATIONAL, NONE
}

fun evaluateGlucoseWithDiabetesType(
    unit: GlucoseUnitType,
    glucoseConcentration: Double,
    afterMedication: Boolean,
    emptyStomach: Boolean,
    diabetesType: DiabetesType
): String {
    val concentrationMgDl = when (unit) {
        GlucoseUnitType.MG_PER_DL -> glucoseConcentration
        GlucoseUnitType.MMOL_PER_L -> glucoseConcentration * 18.0182
    }

    val baseMessage = if (emptyStomach) {
        // Ocena na czczo (dla cukrzycy ciążowej można być bardziej rygorystycznym)
        val fastingThresholds = when (diabetesType) {
            DiabetesType.GESTATIONAL -> listOf(60.0, 90.0, 110.0, 126.0) // bardziej restrykcyjne normy?
            else -> listOf(70.0, 99.9, 125.9, 126.0)
        }

        when {
            concentrationMgDl < fastingThresholds[0] -> "Hipoglikemia (poziom glukozy poniżej normy)"
            concentrationMgDl <= fastingThresholds[1] -> "Prawidłowy poziom glukozy na czczo"
            concentrationMgDl <= fastingThresholds[2] -> "Stan przedcukrzycowy (nieprawidłowa glikemia na czczo)"
            concentrationMgDl >= fastingThresholds[3] -> "Wskazuje na cukrzycę (potwierdź badaniem powtórnym)"
            else -> "Nieprawidłowa wartość pomiaru"
        }
    } else {
        // Ocena po posiłku
        val postMealThresholds = when (diabetesType) {
            DiabetesType.GESTATIONAL -> listOf(120.0, 140.0, 180.0, 200.0) // też bardziej restrykcyjne?
            else -> listOf(140.0, 199.9, 200.0, Double.MAX_VALUE)
        }

        when {
            concentrationMgDl < postMealThresholds[0] -> "Prawidłowy poziom glukozy po posiłku"
            concentrationMgDl <= postMealThresholds[1] -> "Stan przedcukrzycowy (nieprawidłowa glikemia poposiłkowa)"
            concentrationMgDl >= postMealThresholds[2] -> "Wskazuje na cukrzycę (potwierdź badaniem powtórnym)"
            else -> "Nieprawidłowa wartość pomiaru"
        }
    }

    val medsInfo = if (afterMedication) {
        "Uwaga: pomiar wykonany po zażyciu leków obniżających glukozę, interpretuj ostrożnie."
    } else ""

    val diabetesInfo = when (diabetesType) {
        DiabetesType.TYPE_1 -> "Pacjent z cukrzycą typu 1 wymaga ścisłego monitorowania."
        DiabetesType.TYPE_2 -> "Pacjent z cukrzycą typu 2 zaleca regularne konsultacje i kontrolę."
        DiabetesType.MODY -> "Cukrzyca MODY – rzadsza forma, wymaga indywidualnej opieki."
        DiabetesType.LADA -> "Cukrzyca LADA – postać pośrednia, monitoruj rozwój choroby."
        DiabetesType.GESTATIONAL -> "Cukrzyca ciążowa – wymaga rygorystycznej kontroli glikemii."
        DiabetesType.NONE -> ""
    }

    return listOf(baseMessage, medsInfo, diabetesInfo)
        .filter { it.isNotBlank() }
        .joinToString(" ")
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





