package pl.example.aplikacja.Screens

import android.os.Handler
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import kotlinx.coroutines.delay
import pl.example.aplikacja.UiElements.GlucoseChart
import pl.example.aplikacja.UiElements.HeartbeatChart
import pl.example.aplikacja.UiElements.ItemView
import pl.example.aplikacja.removeQuotes
import pl.example.aplikacja.viewModels.AllResultsScreenViewModel
import pl.example.networkmodule.getToken

@Preview(
    name = "Standard", group = "First", device = "id:pixel_8"
)
@Composable
fun AllResultsScreenPreview() {
    AllResultsScreen(NavController(LocalContext.current))
}


@Composable
fun AllResultsScreen(navController: NavController, type: Boolean? = null) {
    val context = LocalContext.current
    var screenType: Boolean? = null
    if (type != null) {
        screenType = !type
    }

    Log.d("SCREEN_ALL", "screenType: $screenType")
    val decoded: DecodedJWT = JWT.decode(getToken(context))
    val viewModel = remember {
        AllResultsScreenViewModel(
            context, removeQuotes(decoded.getClaim("userId").toString())
        )
    }

    val isLoading by viewModel.isLoading.collectAsState()
    val glucoseResults by viewModel.glucoseResults.collectAsState()
    val heartbeatResult by viewModel.heartbeatResult.collectAsState()

    val glucoseResultsData by viewModel.glucoseResultsData.collectAsState()
    Log.d("ALL", "glucoseResultsData: $glucoseResultsData")

    var checked by remember { mutableStateOf(screenType ?: true) }
    var totalDrag by remember { mutableStateOf(0f) }

    Box(Modifier.fillMaxSize()) {
        if (isLoading) {
            Box(
                contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
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
            Column(
                Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(onDragEnd = {
                            if (totalDrag > 100f) {
                                checked = true
                            } else if (totalDrag < -100f) {
                                checked = false
                            }
                            totalDrag = 0f
                        }) { change, dragAmount ->
                            totalDrag += dragAmount
                        }
                    }) {
                Box(Modifier.align(Alignment.CenterHorizontally)) {
//                    Switch(
//                        checked = checked,
//                        onCheckedChange = {
//                            checked = it
//                        },
//                        colors = SwitchDefaults.colors(
//                            checkedThumbColor = MaterialTheme.colorScheme.primary,
//                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
//                            uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
//                            uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
//                        ),
//                        modifier = Modifier
//                            .padding(16.dp)
//                            .align(Alignment.TopCenter)
//                    )
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
                        if (glucoseResults.isNotEmpty()) {
                            item {
                                GlucoseChart(glucoseResults.reversed().take(14))
                            }
                        }
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

                        if (heartbeatResult.isNotEmpty()) {
                            item {
                                HeartbeatChart(heartbeatResult.reversed().take(14))
                            }
                        }
                        items(heartbeatResult) { item ->
                            Row(Modifier.animateItem()) {
                                ItemView(item) { itemId ->
                                    navController.navigate("heartbeat_result/$itemId")
                                }
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
    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(8.dp)
        ) {
            SelectedScreenBottomImage(checked)
        }

    }

}

@Preview
@Composable
fun SelectedScreenBottomImagePreview() {
    SelectedScreenBottomImage(true)
}

@Composable
fun SelectedScreenBottomImage(position: Boolean) {
    //Column(verticalArrangement = Arrangement.Top) {

    AnimatedContent(position, label = "") {
        Box(
            Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    Brush.linearGradient(
                        colors = if (it) {
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                Color.Transparent,
                                Color.Transparent,
                                Color.Transparent
                            )
                        } else {
                            listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Transparent,
                                MaterialTheme.colorScheme.primary
                            )
                        }, start = Offset.Zero, end = Offset.Infinite
                    )
                )
        ) {
            Text(
                text = if (it) "Glukoza" else "Ciśnienie",
                fontSize = 6.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.align(if (it) Alignment.TopStart else Alignment.TopEnd).padding(horizontal = 4.dp)
            )
        }
        //}
    }
}




