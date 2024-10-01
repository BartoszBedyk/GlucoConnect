package pl.example.aplikacja.elements


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlin.reflect.KFunction0

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionDialog(
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: ()-> Unit,
    onOkClick: ()-> Unit,
    onGotToSettingsClick: Unit,
    modifier: Modifier = Modifier
){
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        properties = DialogProperties(),
        content = {
            Column(modifier.fillMaxWidth()) {
                Text(
                    text = "Permission required",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = permissionTextProvider.getDescription(
                        isPermanentlyDeclined = isPermanentlyDeclined
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                HorizontalDivider()
                Text(
                    text = if (isPermanentlyDeclined) {
                        "Grant permission"
                    } else {
                        "OK"
                    },
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isPermanentlyDeclined) {
                                onGotToSettingsClick
                            } else {
                                onOkClick()
                            }
                        }
                        .padding(16.dp)
                )
            }
        }
    )



}

interface PermissionTextProvider{
    fun getDescription(isPermanentlyDeclined: Boolean): String
}

class BluetoothPermissionTextProvider : PermissionTextProvider{
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined){
            "It seams you permanently declinex bl permission go to app setting "
        } else {
            "This app need access to your bluetooth."
        }
    }

}
class BluetoothScanPermissionTextProvider : PermissionTextProvider{
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined){
            "It seams you permanently declined scan permission go to app setting "
        } else {
            "This app need access to your sanning."
        }
    }

}

class BluetoothAdminPermissionTextProvider : PermissionTextProvider{
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined){
            "It seams you permanently dec  admin bl permission go to app setting "
        } else {
            "This app need access to your bluetooth."
        }
    }

}

class BluetoothConnectAdminPermissionTextProvider : PermissionTextProvider{
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined){
            "It seams you permanently declinex connect permission go to app setting "
        } else {
            "This app need access to your bluetooth."
        }
    }

}