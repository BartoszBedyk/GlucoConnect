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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.example.aplikacja.formatDateTimeSpecificLocale
import pl.example.aplikacja.formatUnit
import pl.example.networkmodule.apiData.HeartbeatResult
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

@Composable
fun ItemView(item: HeartbeatResult, onItemClick: (String) -> Unit) {

    Column(modifier = Modifier.fillMaxWidth()
        .clickable { onItemClick(item.id.toString()) }
        //.background(chooseColorForGlucose(item.glucoseConcentration, item.unit))
    ) {
        Text(
            text = "Ciśnienie: ${item.systolicPressure}" + "/" + "${item.diastolicPressure}" +"  " + "${item.pulse}",
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

private fun chooseColorForGlucose(glucose: Double, unit: GlucoseUnitType): Brush {
    return when (unit) {
        GlucoseUnitType.MMOL_PER_L -> {
            if (glucose < 5.5 && glucose > 3.9) {
                Brush.linearGradient(listOf(Color.White, Color.Green), start = Offset(0.5f, 0.5f), end = Offset.Infinite)
            } else if (glucose > 5.5) {
                Brush.linearGradient(listOf(Color.White,Color.Yellow))
            } else if (glucose < 3.9) {
                Brush.linearGradient(listOf(Color.White,Color.Red))
            }
            else {
                Brush.linearGradient(listOf(Color.White,Color.White))
            }
        }

        GlucoseUnitType.MG_PER_DL -> {
            if (glucose < 99.0 && glucose > 70.0) {
                Brush.linearGradient(listOf(Color.White,Color.Green), start = Offset(1.1f, 1.1f), end = Offset(0.8f, 0.8f))
            } else if (glucose > 99.0) {
                Brush.linearGradient(listOf(Color.White,Color.Yellow), start = Offset(0.5f, 0.5f), end = Offset(0.8f, 0.8f))
            } else if (glucose < 70.0) {
                Brush.linearGradient(listOf(Color.White,Color.Red))
            }
            else {
                Brush.linearGradient(listOf(Color.White,Color.White))
            }
        }

    }
}



