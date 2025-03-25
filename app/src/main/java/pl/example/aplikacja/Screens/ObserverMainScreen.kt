package pl.example.aplikacja.Screens

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import pl.example.aplikacja.UiElements.ItemView
import pl.example.aplikacja.removeQuotes
import pl.example.aplikacja.viewModels.ObserverMainScreenViewModel
import pl.example.networkmodule.getToken

@Composable
fun ObserverMainScreen(navController: NavController) {
    val context = LocalContext.current
    val decoded: DecodedJWT = JWT.decode(getToken(context))
    val viewModel = remember {
        ObserverMainScreenViewModel(
            context,
            removeQuotes(decoded.getClaim("userId").toString())
        )
    }


    val isLoading by viewModel.isLoading.collectAsState()
    val unAccepted by viewModel.observedUnaccepted.collectAsState()
    val acceptedUsers by viewModel.observedAcceptedUser.collectAsState()
    val snackState = remember { SnackbarHostState() }

    Box(Modifier.fillMaxSize()) {
        if (isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Column {
                    CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                    Text(
                        text = "Nawiązywanie połączenia...",
                        modifier = Modifier.padding(16.dp),
                        color = androidx.compose.ui.graphics.Color.Gray
                    )
                }
            }
        } else {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {
                Text(text = "Obserwowani użytkownicy", fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(acceptedUsers) { item ->
                        Row {
                            ItemView(item) { userId ->
                                navController.navigate("main_screen/$userId")
                            }
                        }
                    }

                    items(unAccepted) { item ->
                        Row {
                            ItemView(item) {
                            }
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { navController.navigate("user_profile_screen") },
                    shape = Shapes().medium,
                    modifier = Modifier.padding(16.dp).align(Alignment.End),
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)

                ) {
                    Icon(Icons.Filled.Add, "Przycisk do dodawania wyników")
                }
            }
        }
    }
}
