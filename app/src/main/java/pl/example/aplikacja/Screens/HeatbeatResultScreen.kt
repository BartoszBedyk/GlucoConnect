package pl.example.aplikacja.Screens



import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import pl.example.aplikacja.formatDateTimeSpecificLocale
import pl.example.aplikacja.viewModels.HeartbeatDetailsScreenViewModel

@Composable
fun HeartbeatResultScreen(id: String) {
    val context = LocalContext.current
    val viewModel = HeartbeatDetailsScreenViewModel(context, id)

    val heartbeatResult by viewModel.heartbeatResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    if (isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                CircularProgressIndicator()
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
                    text = "Dane pomiaru tętna",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                TextRow(label = "ID pomiaru", value = heartbeatResult?.id.toString())
                TextRow(label = "Czas Pomiaru", value = heartbeatResult?.timestamp?.let {
                    formatDateTimeSpecificLocale(it)
                } ?: "Brak danych")
                TextRow(label = "Ciśnienie skurczowe", value = "${heartbeatResult?.systolicPressure} mmHg")
                TextRow(label = "Ciśnienie rozkurczowe", value = "${heartbeatResult?.diastolicPressure} mmHg")
                TextRow(label = "Puls", value = "${heartbeatResult?.pulse} bpm")
                TextRow(label = "Notatka", value = heartbeatResult?.note ?: "Brak danych")
            }
        }
    }
}


