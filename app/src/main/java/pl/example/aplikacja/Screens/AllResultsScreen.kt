package pl.example.aplikacja.Screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import pl.example.aplikacja.UiElements.ItemView
import pl.example.aplikacja.removeQuotes
import pl.example.aplikacja.viewModels.AllResultsScreenViewModel
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.getToken

@Preview(
    name = "Standard", group = "First",
    device = "id:pixel_8"
)
@Composable
fun AllResultsScreenPreview() {
    AllResultsScreen(NavController(LocalContext.current))
}


@Composable
fun AllResultsScreen(navController: NavController, type: Boolean? = null) {
    val context = LocalContext.current

    val apiProvider = ApiProvider(context)
    val decoded: DecodedJWT = JWT.decode(getToken(context))
    val viewModel = remember { AllResultsScreenViewModel(apiProvider, removeQuotes(decoded.getClaim("userId").toString()))  }

    val glucoseResults by viewModel.glucoseResults.collectAsState()
    val heartbeatResult by viewModel.heartbeatResult.collectAsState()
    var checked by remember { mutableStateOf(type ?: true) }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Box(Modifier.align(Alignment.CenterHorizontally)) {
                Switch(
                    checked = checked,
                    onCheckedChange = {
                        checked = it
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                        uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                        uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopCenter)
                )
            }


            if (checked) {
                LazyColumn(
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    userScrollEnabled = true,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(glucoseResults) { item ->
                        Row(Modifier.animateItem()) {
                            ItemView(item) { itemId ->
                                navController.navigate("glucose_result/$itemId")
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(heartbeatResult) { item ->
                        Row(Modifier.animateItem()) {
                            ItemView(item) { itemId ->
                                navController.navigate("glucose_result/$itemId")
                            }
                        }
                    }
                }
            }
        }


        FloatingActionButton(
            onClick = {
                if (checked) {
                    navController.navigate("add_glucose_result")
                } else {
                    navController.navigate("add_heartbeat_result")
                }
            },
            shape = Shapes().medium,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        ) {
            Icon(Icons.Filled.Add, "Przycisk do dodawania wyników")
        }
    }
}

