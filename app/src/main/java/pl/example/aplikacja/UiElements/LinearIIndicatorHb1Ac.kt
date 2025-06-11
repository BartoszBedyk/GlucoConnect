package pl.example.aplikacja.UiElements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.example.networkmodule.apiData.enumTypes.DiabetesType

@Composable
fun LinearIndicatorHb1Ac(value: Float, diabetesType: DiabetesType) {
    var currentProgress by remember { mutableFloatStateOf(0f) }
    currentProgress = value / 9.0f



    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Wartość Hb1Ac: $value %",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 8.dp, horizontal = 16.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 18.sp
        )
        LinearProgressIndicator(
            progress = { currentProgress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp, bottom = 4.dp, start = 16.dp, end = 16.dp),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        HorizontalDivider(
            modifier = Modifier.padding(
                top = 4.dp,
                bottom = 4.dp,
                start = 16.dp,
                end = 16.dp
            ), thickness = 1.dp
        )
        Text(
            text = interpretHbA1c(value, diabetesType),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp, bottom = 0.dp, start = 16.dp, end = 16.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 12.sp
        )
        Text(
            text = "HbA1c (hemoglobina glikowana) wskazuje na średni poziom glukozy we krwi w ciągu ostatnich około 2–3 miesięcy. Jest to kluczowy wskaźnik w diagnostyce i monitorowaniu cukrzycy.",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 0.dp, bottom = 4.dp, start = 16.dp, end = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 8.sp,
            lineHeight = 12.sp
        )

    }
}

@Preview
@Composable
fun LinearIndicatorHb1AcPreview() {
    LinearIndicatorHb1Ac(value = 5.0f, DiabetesType.TYPE_1)
}


fun interpretHbA1c(value: Float, type: DiabetesType): String {
    return when (type) {
        DiabetesType.NONE -> when {
            value < 5.7 -> "Prawidłowy poziom glikemii"
            value in 5.7..6.4 -> "Stan przedcukrzycowy"
            value >= 6.5 -> "Podejrzenie cukrzycy (wymaga potwierdzenia)"
            else -> "Nieprawidłowa wartość"
        }

        DiabetesType.TYPE_1, DiabetesType.LADA -> when {
            value < 6.5 -> "Dobrze kontrolowana cukrzyca typu 1 / LADA"
            value < 7.0 -> "Akceptowalna kontrola glikemii"
            else -> "Niewyrównana cukrzyca typu 1 / LADA"
        }

        DiabetesType.TYPE_2, DiabetesType.MODY -> when {
            value < 6.5 -> "Dobrze kontrolowana cukrzyca typu 2 / MODY"
            value < 7.0 -> "Akceptowalna kontrola glikemii"
            value < 8.0 -> "Średnia kontrola, zalecana poprawa"
            else -> "Niewyrównana cukrzyca typu 2 / MODY"
        }

        DiabetesType.GESTATIONAL -> when {
            value < 6.0 -> "Dobrze kontrolowana cukrzyca ciążowa"
            else -> "Niewyrównana cukrzyca ciążowa – ryzyko dla matki i płodu"
        }
    }
}
