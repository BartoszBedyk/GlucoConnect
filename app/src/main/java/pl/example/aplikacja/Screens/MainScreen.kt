package pl.example.aplikacja.Screens

import MainScreenViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
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
import pl.example.aplikacja.UiElements.ItemView
import pl.example.aplikacja.removeQuotes
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.getToken

@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current

    val apiProvider = ApiProvider(context)

    val decoded: DecodedJWT = JWT.decode(getToken(context))
    val viewModel =
        MainScreenViewModel(apiProvider, removeQuotes(decoded.getClaim("userId").toString()))
    val items by viewModel.threeGlucoseItems.collectAsState()


//    val prefUnit by viewModel.prefUnit.collectAsState()
    //LineChartCompose()

    Box {


        LazyColumn(
            modifier = Modifier
                .padding(bottom = 0.dp)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                ItemView(item) { itemId ->
                    navController.navigate("glucose_result/$itemId")
                }
            }
        }
        ExpandableFloatingActionButton(navController);

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