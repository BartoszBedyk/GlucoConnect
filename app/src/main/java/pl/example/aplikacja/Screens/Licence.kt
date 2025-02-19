package pl.example.aplikacja.Screens

import android.graphics.drawable.GradientDrawable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import pl.example.aplikacja.R


@Composable
fun LicenceScreen(typUmowy: String) {
    val context = LocalContext.current

    if(typUmowy == "licencyjna"){
        val inputStream = context.resources.openRawResource(R.raw.umowa)
        val text = inputStream.bufferedReader().use { it.readText() }
        Card() {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(text = text)
            }
        }
    }
    else if(typUmowy == "nielicencyjna"){
        val inputStream = context.resources.openRawResource(R.raw.umowa_download)
        val text = inputStream.bufferedReader().use { it.readText() }
        Card() {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(text = text)
            }
        }
    }

}