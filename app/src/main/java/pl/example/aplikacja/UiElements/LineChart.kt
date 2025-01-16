package pl.example.aplikacja.UiElements


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.Line


@Preview(name = "Standard", group = "First")
@Composable
fun LineChartComposePreview() {
    LineChartCompose()
}

@Composable
fun LineChartCompose() {
    val values = listOf<Double>(1.0, 2.0, 3.0, 4.0, 5.0)

    val data = listOf(
        Line(
            label = "Glukoza",
            color = Brush.linearGradient(),
            values = values
        )
    )
    LineChart(modifier = Modifier.fillMaxSize(), data = data)


}