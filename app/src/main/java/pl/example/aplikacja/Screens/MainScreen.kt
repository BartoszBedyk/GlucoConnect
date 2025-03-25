package pl.example.aplikacja.Screens

import MainScreenViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import pl.example.aplikacja.R
import pl.example.aplikacja.UiElements.GlucoseChart
import pl.example.aplikacja.UiElements.HeartbeatChart
import pl.example.aplikacja.UiElements.ItemView
import pl.example.aplikacja.removeQuotes
import pl.example.aplikacja.toUserType
import pl.example.networkmodule.apiData.enumTypes.UserType
import pl.example.networkmodule.getToken
import pl.example.networkmodule.saveToken

@Composable
fun MainScreen(navController: NavController, userId: String?) {
    val context = LocalContext.current

    if (getToken(context) == null) navController.navigate("login_screen");

    val decoded: DecodedJWT = JWT.decode(getToken(context))
    val userType = toUserType(decoded.getClaim("userType").asString())
    val decodedUserId = removeQuotes(decoded.getClaim("userId").asString())

    LaunchedEffect(userId) {
        if (userId == null) {
            when (userType) {
                UserType.PATIENT -> return@LaunchedEffect
                UserType.DOCTOR -> navController.navigate("download_results")
                UserType.ADMIN -> navController.navigate("admin_main_screen")
                UserType.OBSERVER -> navController.navigate("observer_main_screen")
            }
        }
    }


    val viewModel = remember {
        MainScreenViewModel(
            context,
            userId ?: decodedUserId
        )
    }


    val glucoseItems by viewModel.threeGlucoseItems.collectAsState()
    val heartbeatItems by viewModel.heartbeatItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()



    Box(modifier = Modifier.fillMaxSize()) {
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                if (glucoseItems.isNotEmpty()) {
                    item {
                        GlucoseChart(glucoseItems.reversed())
                    }
                    items(glucoseItems) { item ->
                        ItemView(item) { itemId ->
                            navController.navigate("glucose_result/$itemId")
                        }
                    }
                }


                if (heartbeatItems.isNotEmpty()) {
                    item {
                        HeartbeatChart(heartbeatItems.reversed())
                    }
                    items(heartbeatItems) { item ->
                        ItemView(item) { itemId ->
                            navController.navigate("heartbeat_result/$itemId")
                        }
                    }
                }
            }
        }
        if(userType==UserType.PATIENT){
            ExpandableFloatingActionButton(navController)
        }

    }
}


@Composable
fun ExpandableFloatingActionButton(navController: NavController) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(FloatingActionButtonDefaults.containerColor)
                .shadow(elevation = 8.dp)
        ) {

            AnimatedVisibility(
                visible = isExpanded, modifier = Modifier
                    .padding(end = 0.dp)
            ) {
                Row(
                ) {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate("add_glucose_result/main")
                            isExpanded = !isExpanded
                        },
                        modifier = Modifier.padding(end = 0.dp),
                        elevation = FloatingActionButtonDefaults.elevation(0.dp)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.drop_icon),
                            contentDescription = "Icon",
                            modifier = Modifier.size(24.dp)
                        )
                    }


                    FloatingActionButton(
                        onClick = {
                            navController.navigate("add_heartbeat_result/main")
                            isExpanded = !isExpanded
                        },
                        elevation = FloatingActionButtonDefaults.elevation(0.dp)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.monitor_heart),
                            contentDescription = "Icon",
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
            }


            FloatingActionButton(
                onClick = { isExpanded = !isExpanded },
                shape = Shapes().medium,
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.Close else Icons.Filled.Add,
                    contentDescription = if (isExpanded) "Zamknij" else "Dodaj"
                )
            }
        }
    }
}