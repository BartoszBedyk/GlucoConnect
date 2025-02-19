package pl.example.aplikacja.Screens


import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pl.example.aplikacja.viewModels.DownloadViewModel
import pl.example.aplikacja.viewModels.DownloadViewModelFactory
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiMethods.ApiProvider
import java.io.File

@Composable
fun AllResultsDownload(navController: NavController) {
    val context = LocalContext.current
    val viewModel: DownloadViewModel =
        viewModel(factory = DownloadViewModelFactory(ApiProvider(context)))

    val isLoading by viewModel.isLoading.collectAsState()
    val allResults by viewModel.glucoseResults.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { downloadDataAsJson(context, allResults) },
                modifier = Modifier.padding(8.dp).fillMaxWidth()
            ) {
                Text("Pobierz dane z serwera")
            }
//            Button(
//                onClick = {
//                    openFile(
//                        context,
//                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/glucose_results.json"
//                    )
//                },
//                modifier = Modifier.padding(8.dp).fillMaxWidth()
//            ) {
//                Text("Otwórz plik JSON")
//            }

            Text(
                text = "Pobierając dane zgadzasz się na warunki umowy o wykorzystaniu danych aplikacji GlucoMaxxConnect",
                modifier = Modifier.padding(vertical = 8.dp)
                    .clickable { navController.navigate("licence_screen/nielicencyjna") }
            )
        }
    }
}
fun downloadDataAsJson(context: Context, allResults: List<ResearchResult>) {
    val jsonString = Json.encodeToString(allResults)
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(downloadsDir, "glucose_results.json")
    file.writeText(jsonString)
}

fun openFile(context: Context, filePath: String) {
    val file = File(filePath)
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(uri, "application/json")
    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    context.startActivity(Intent.createChooser(intent, "Otwórz plik JSON"))
}



