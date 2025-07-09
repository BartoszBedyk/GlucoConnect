package pl.example.aplikacja.UiElements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.example.aplikacja.Screens.TextRow
import pl.example.aplikacja.mappters.formatDateTimeSpecificLocale
import pl.example.aplikacja.mappters.formatUnit
import pl.example.networkmodule.apiData.HeartbeatResult
import pl.example.networkmodule.apiData.ObserverResult
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiData.UserResult
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType


@Composable
fun ItemView(item: ResearchResult, onItemClick: (String) -> Unit) {

    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { onItemClick(item.id.toString()) }
        .background(chooseColorForGlucose(item.glucoseConcentration, item.unit))) {
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
fun ItemView(item: UserResult, onItemClick: (String) -> Unit) {

    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { onItemClick(item.id.toString()) }) {
        TextRow(label = "Imię i nazwisko", value = item.firstName + " " + item.lastName)
        TextRow(label = "Status", value = "Zaakceptowany")
        HorizontalDivider(thickness = 1.dp)
    }
}

@Composable
fun ItemView(item: ObserverResult, onItemClick: (String) -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { onItemClick(item.id.toString()) }) {
        TextRow(label = "Identyfikator użytkownika", value = item.observerId.toString())
        TextRow(label = "Status", value = "Oczekujący akceptacji")
        HorizontalDivider(thickness = 1.dp)
    }
}


@Composable
fun ItemView(item: HeartbeatResult, onItemClick: (String) -> Unit) {

    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { onItemClick(item.id.toString()) }
        //.background(chooseColorForGlucose(item.glucoseConcentration, item.unit))
    ) {
        Text(
            text = "Ciśnienie: ${item.systolicPressure}" + "/" + "${item.diastolicPressure}" + "  " + "${item.pulse}",
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
                Brush.linearGradient(
                    colors = listOf(
                        Color.Green,
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent
                    ), start = Offset(900f, 0f), end = Offset(0f, 0f)
                )
            } else if (glucose > 5.5) {
                Brush.linearGradient(
                    colors = listOf(
                        Color.Green,
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent
                    ), start = Offset(900f, 0f), end = Offset(0f, 0f)
                )
            } else if (glucose < 3.9) {
                Brush.linearGradient(
                    colors = listOf(
                        Color.Red,
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent
                    ), start = Offset(900f, 0f), end = Offset(0f, 0f)
                )
            } else {
                Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
            }
        }

        GlucoseUnitType.MG_PER_DL -> {
            if (glucose < 99.0 && glucose > 70.0) {
                Brush.linearGradient(
                    colors = listOf(
                        Color.Green,
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent
                    ), start = Offset(900f, 0f), end = Offset(0f, 0f)
                )
            } else if (glucose > 99.0) {
                Brush.linearGradient(
                    colors = listOf(
                        Color.Yellow,
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent
                    ), start = Offset(900f, 0f), end = Offset(0f, 0f)
                )
            } else if (glucose < 70.0) {
                Brush.linearGradient(
                    colors = listOf(
                        Color.Red,
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent
                    ), start = Offset(900f, 0f), end = Offset(0f, 0f)
                )
            } else {
                Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
            }
        }

    }
}



