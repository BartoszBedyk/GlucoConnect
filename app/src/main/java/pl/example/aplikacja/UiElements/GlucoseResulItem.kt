package pl.example.aplikacja.UiElements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.example.aplikacja.formatDateTimeSpecificLocale
import pl.example.aplikacja.formatUnit
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType


@Composable
fun ItemView(item: ResearchResult, onItemClick: (String) -> Unit) {

    Column(modifier = Modifier.fillMaxWidth()
        .clickable { onItemClick(item.id.toString()) }
        .background(chooseColorForGlucose(item.glucoseConcentration, item.unit))
    ) {
        Text(
            text = "Glukoza: ${item.glucoseConcentration} ${formatUnit(item.unit)}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = formatDateTimeSpecificLocale(item.timestamp),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun chooseColorForGlucose(glucose: Double, unit: GlucoseUnitType): androidx.compose.ui.graphics.Color {
    return when (unit) {
        GlucoseUnitType.MMOL_PER_L -> {
            if (glucose < 5.5 && glucose > 3.9) {
                androidx.compose.ui.graphics.Color.Green
            } else if (glucose > 5.5) {
               androidx.compose.ui.graphics.Color.Yellow
            } else if (glucose < 3.9) {
                androidx.compose.ui.graphics.Color.Red
            }
            else {
                androidx.compose.ui.graphics.Color.White
            }
        }

        GlucoseUnitType.MG_PER_DL -> {
            if (glucose < 99.0 && glucose > 70.0) {
                 androidx.compose.ui.graphics.Color.Green
            } else if (glucose > 99.0) {
                androidx.compose.ui.graphics.Color.Yellow
            } else if (glucose < 70.0) {
                 androidx.compose.ui.graphics.Color.Red
            }
            else {
                androidx.compose.ui.graphics.Color.White
            }
        }

    }
}



