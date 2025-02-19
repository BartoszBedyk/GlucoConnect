package pl.example.aplikacja.UiElements

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.aspectRatio

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.LineProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.StrokeStyle
import pl.example.networkmodule.apiData.HeartbeatResult
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import java.util.Date
import java.util.UUID

@Preview
@Composable
fun LineChartPreview() {
    val userId = UUID.randomUUID()
    val id = UUID.randomUUID().toString()
     val results =listOf(
        ResearchResult(
            id = UUID.fromString(id),
            sequenceNumber = 1,
            glucoseConcentration = 5.5,
            unit = GlucoseUnitType.MMOL_PER_L,
            timestamp = Date(),
            userId = userId,
            deletedOn = null,
            lastUpdatedOn = Date()
        ),
        ResearchResult(
            id = UUID.randomUUID(),
            sequenceNumber = 2,
            glucoseConcentration = 4.8,
            unit = GlucoseUnitType.MMOL_PER_L,
            timestamp = Date(),
            userId = userId,
            deletedOn = null,
            lastUpdatedOn = Date()
        ),
        ResearchResult(
            id = UUID.randomUUID(),
            sequenceNumber = 3,
            glucoseConcentration = 126.1,
            unit = GlucoseUnitType.MG_PER_DL,
            timestamp = Date(),
            userId = userId,
            deletedOn = null,
            lastUpdatedOn = Date()
        ),
        ResearchResult(
            id = UUID.fromString(id),
            sequenceNumber = 1,
            glucoseConcentration = 5.5,
            unit = GlucoseUnitType.MMOL_PER_L,
            timestamp = Date(),
            userId = userId,
            deletedOn = null,
            lastUpdatedOn = Date()
        ),
        ResearchResult(
            id = UUID.randomUUID(),
            sequenceNumber = 2,
            glucoseConcentration = 4.8,
            unit = GlucoseUnitType.MMOL_PER_L,
            timestamp = Date(),
            userId = userId,
            deletedOn = null,
            lastUpdatedOn = Date()
        ),
        ResearchResult(
            id = UUID.randomUUID(),
            sequenceNumber = 3,
            glucoseConcentration = 126.1,
            unit = GlucoseUnitType.MG_PER_DL,
            timestamp = Date(),
            userId = userId,
            deletedOn = null,
            lastUpdatedOn = Date()
        ),
        ResearchResult(
            id = UUID.fromString(id),
            sequenceNumber = 1,
            glucoseConcentration = 5.5,
            unit = GlucoseUnitType.MMOL_PER_L,
            timestamp = Date(),
            userId = userId,
            deletedOn = null,
            lastUpdatedOn = Date()
        ),
        ResearchResult(
            id = UUID.randomUUID(),
            sequenceNumber = 2,
            glucoseConcentration = 4.8,
            unit = GlucoseUnitType.MMOL_PER_L,
            timestamp = Date(),
            userId = userId,
            deletedOn = null,
            lastUpdatedOn = Date()
        ),
        ResearchResult(
            id = UUID.randomUUID(),
            sequenceNumber = 3,
            glucoseConcentration = 126.1,
            unit = GlucoseUnitType.MG_PER_DL,
            timestamp = Date(),
            userId = userId,
            deletedOn = null,
            lastUpdatedOn = Date()
        )
    )

    GlucoseChart(glucoseData = results)
}
@Composable
fun HeartbeatChart(heartbeatData: List<HeartbeatResult>) {
    val lines = listOf(
        Line(
            label = "Ciśnienie rozkurczowe",
            values = heartbeatData.map { it.diastolicPressure.toDouble() },
            color = Brush.linearGradient(
                colors = listOf(Color.Red, Color.Yellow)
            ),
            firstGradientFillColor = Color.Blue.copy(alpha = 0.3f),
            secondGradientFillColor = Color.Cyan.copy(alpha = 0.1f),
            drawStyle = DrawStyle.Stroke(3.dp),
            strokeAnimationSpec = tween(1500),
            gradientAnimationSpec = tween(1500),
            gradientAnimationDelay = 500L,
            dotProperties = DotProperties(
                enabled = true,
                radius = 5.dp,
                color = SolidColor(Color.Red),
                strokeWidth = 2.dp,
                strokeColor = SolidColor(Color.Black),
                animationEnabled = true,
                animationSpec = tween(500)
            ),
            popupProperties = PopupProperties(
                textStyle = androidx.compose.ui.text.TextStyle.Default.copy(
                    color = Color.Transparent,
                    fontSize = 0.sp
                )
            ),
            curvedEdges = true
        ),
        Line(
            label = "Ciśnienie skurczowe",
            values = heartbeatData.map { it.systolicPressure.toDouble() },
            color = Brush.linearGradient(
                colors = listOf(Color.Blue, Color.Cyan)
            ),
            firstGradientFillColor = Color.Blue.copy(alpha = 0.3f),
            secondGradientFillColor = Color.Cyan.copy(alpha = 0.1f),
            drawStyle = DrawStyle.Stroke(3.dp),
            strokeAnimationSpec = tween(1500),
            gradientAnimationSpec = tween(1500),
            gradientAnimationDelay = 500L,
            dotProperties = DotProperties(
                enabled = true,
                radius = 5.dp,
                color = SolidColor(Color.Red),
                strokeWidth = 2.dp,
                strokeColor = SolidColor(Color.Black),
                animationEnabled = true,
                animationSpec = tween(500)
            ),
            popupProperties = PopupProperties(
                textStyle = androidx.compose.ui.text.TextStyle.Default.copy(
                    color = Color.Transparent,
                    fontSize = 0.sp
                )
            ),
            curvedEdges = true
        ),
        Line(
            label = "Puls",
            values = heartbeatData.map { it.pulse.toDouble() },
            color = Brush.linearGradient(
                colors = listOf(Color.Yellow, Color.White)
            ),
            firstGradientFillColor = Color.Blue.copy(alpha = 0.3f),
            secondGradientFillColor = Color.Cyan.copy(alpha = 0.1f),
            drawStyle = DrawStyle.Stroke(3.dp),
            strokeAnimationSpec = tween(1500),
            gradientAnimationSpec = tween(1500),
            gradientAnimationDelay = 500L,
            dotProperties = DotProperties(
                enabled = true,
                radius = 5.dp,
                color = SolidColor(Color.Red),
                strokeWidth = 2.dp,
                strokeColor = SolidColor(Color.Black),
                animationEnabled = true,
                animationSpec = tween(500)
            ),
            popupProperties = PopupProperties(
                textStyle = androidx.compose.ui.text.TextStyle.Default.copy(
                    color = Color.Transparent,
                    fontSize = 0.sp
                )
            ),
            curvedEdges = true
        )
    )

    LineChart(
        modifier = Modifier.fillMaxWidth()
            .aspectRatio(16 / 9f)
            .padding(16.dp)
            .wrapContentHeight(),
        data = lines,
        curvedEdges = true,
        animationDelay = 300L,
        dividerProperties = DividerProperties(
            //linie wykresy XY
            enabled = false,
            xAxisProperties = LineProperties(
                enabled = true
            ),
            yAxisProperties = LineProperties(
                enabled = true
            )
        ),
        gridProperties = GridProperties(
            enabled = true,
            xAxisProperties = GridProperties.AxisProperties(
                thickness = 1.dp,
                color = SolidColor(Color.Transparent)
            ),
            yAxisProperties = GridProperties.AxisProperties(
                thickness = 2.dp,
                color = SolidColor(Color.Transparent)
            )
        ),
        dotsProperties = DotProperties(
            enabled = true,
            radius = 4.dp,
            color = SolidColor(Color.Red)
        ),
        popupProperties = PopupProperties(
            enabled = true,
            textStyle = androidx.compose.ui.text.TextStyle.Default.copy(
                color = Color.Transparent,
                fontSize = 12.sp
            )
        ),
        //te jebane cyfry
        indicatorProperties = HorizontalIndicatorProperties(
            enabled = false,
            textStyle = androidx.compose.ui.text.TextStyle.Default,
            padding = 12.dp
        )
    )
}





@Composable
fun GlucoseChart(glucoseData: List<ResearchResult>) {
    val lines = listOf(
        Line(
            label = "Glukoza",
            values = glucoseData.map { it.glucoseConcentration },
            color = Brush.linearGradient(
                colors = listOf(Color.Blue, Color.Cyan)
            ),
            firstGradientFillColor = Color.Blue.copy(alpha = 0.3f),
            secondGradientFillColor = Color.Cyan.copy(alpha = 0.1f),
            drawStyle = DrawStyle.Stroke(3.dp),
            strokeAnimationSpec = tween(1500),
            gradientAnimationSpec = tween(1500),
            gradientAnimationDelay = 500L,
            dotProperties = DotProperties(
                enabled = true,
                radius = 5.dp,
                color = SolidColor(Color.Red),
                strokeWidth = 2.dp,
                strokeColor = SolidColor(Color.Black),
                animationEnabled = true,
                animationSpec = tween(500)
            ),
            popupProperties = PopupProperties(
                textStyle = androidx.compose.ui.text.TextStyle.Default.copy(
                    color = Color.Transparent,
                    fontSize = 0.sp
                )
            ),
            curvedEdges = true
        )
    )

    LineChart(
        modifier = Modifier.fillMaxWidth()
            .aspectRatio(16 / 9f)
            .padding(16.dp)
            .wrapContentHeight(),
        data = lines,
        curvedEdges = true,
        animationDelay = 300L,
        dividerProperties = DividerProperties(
            //linie wykresy XY
            enabled = false,
            xAxisProperties = LineProperties(
                enabled = true
            ),
            yAxisProperties = LineProperties(
                enabled = true
            )
        ),
        gridProperties = GridProperties(
            enabled = true,
            xAxisProperties = GridProperties.AxisProperties(
                thickness = 1.dp,
                color = SolidColor(Color.Transparent)
            ),
            yAxisProperties = GridProperties.AxisProperties(
                thickness = 2.dp,
                color = SolidColor(Color.Transparent)
            )
        ),
        dotsProperties = DotProperties(
            enabled = true,
            radius = 4.dp,
            color = SolidColor(Color.Red)
        ),
        popupProperties = PopupProperties(
            enabled = true,
            textStyle = androidx.compose.ui.text.TextStyle.Default.copy(
                color = Color.Transparent,
                fontSize = 12.sp
            )
        ),
        //te jebane cyfry
        indicatorProperties = HorizontalIndicatorProperties(
            enabled = false,
            textStyle = androidx.compose.ui.text.TextStyle.Default,
            padding = 12.dp
        )
    )
}