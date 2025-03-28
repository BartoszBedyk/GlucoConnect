package pl.example.aplikacja.UiElements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ColorSquare(isTrue: Boolean) {
    val color = if (isTrue) Color.Green else Color.Red
    Box(
        modifier = Modifier
            .size(100.dp) // Możesz zmienić rozmiar kwadratu
            .background(color)
    )
}

@Preview
@Composable
fun PreviewColorSquare() {
    ColorSquare(isTrue = true)
}
