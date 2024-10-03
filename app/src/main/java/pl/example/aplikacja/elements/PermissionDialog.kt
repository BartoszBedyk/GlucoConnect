package pl.example.aplikacja.elements


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import pl.example.bluetoothmodule.permission.PermissionControl


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionDialog(typ : String, onOkClick: ()-> Unit) {
    var openDialog by remember { mutableStateOf(true) }


    if (openDialog) {
        BasicAlertDialog(
            modifier = Modifier
                .padding(16.dp)
                .background(Color.White),
            onDismissRequest = { openDialog = false },
            properties = DialogProperties(

            )
        ) {
            Surface {
                Column(){
                    Text(
                        text = "Brak uprawnień",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.padding(6.dp))
                    Text(
                        text = "Potrzebujemy uprawnień do $typ"
                    )
                    Spacer(Modifier.padding(6.dp))
                    TextButton(
                        onClick =onOkClick,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                            Text(text = "Uzyskaj")
                    }
                }
            }
        }
    }

}




