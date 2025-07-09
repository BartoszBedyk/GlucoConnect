package pl.example.aplikacja.UiElements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissBackground(dismissState: SwipeToDismissBoxState) {
    val direction = dismissState.dismissDirection

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                when (direction) {
                    SwipeToDismissBoxValue.StartToEnd -> Color.Green.copy(alpha = 0.3f) // np. zielone tło na edycję
                    SwipeToDismissBoxValue.EndToStart -> Color.Red.copy(alpha = 0.3f)  // czerwone tło na usuwanie
                    else -> Color.Transparent
                }
            ),
        contentAlignment = when (direction) {
            SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
            SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
            else -> Alignment.Center
        }
    ) {
        when (direction) {
            SwipeToDismissBoxValue.StartToEnd -> Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = Color.Green,
                modifier = Modifier.padding(16.dp)
            )
            SwipeToDismissBoxValue.EndToStart -> Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
            else -> { /* nic nie pokazuj */ }
        }
    }
}