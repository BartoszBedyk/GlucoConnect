package pl.example.aplikacja.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pl.example.aplikacja.viewModels.AdministrationMainViewModel
import pl.example.networkmodule.apiData.UserResult
import pl.example.networkmodule.apiMethods.ApiProvider

@Composable
fun AdministrationMainScreen(navController: NavController) {

    val context = LocalContext.current
    val apiProvider = remember { ApiProvider(context) }

    val viewModel = remember {
        AdministrationMainViewModel(apiProvider)
    }

    val users = viewModel.users.collectAsState()

    if (users.value.isEmpty()) return Text(
        text = "Brak danych",
        modifier = Modifier.padding(16.dp),
        color = MaterialTheme.colorScheme.onBackground
    )
    LazyColumn {
        items(users.value) { user ->
            UserItem(user) { itemId ->
                navController.navigate("admin_user_direct/$itemId")
            }
        }
    }


}

@Composable
fun UserItem(user: UserResult, onItemClick: (String) -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { onItemClick(user.id.toString()) }
    ) {
        TextRow(
            label = "Dane personalne",
            value = user.firstName + " " + user.lastName,
            fontSize = 20
        )
        TextRow(label = "ID:", value = user.id.toString(), fontSize = 20)
        TextRow(label = "Zablokowany", value = user.isBlocked.toString(), fontSize = 20)
        HorizontalDivider()
    }


}