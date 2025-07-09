package pl.example.aplikacja.UiElements

import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import pl.example.aplikacja.Screens.MedicationItem
import pl.example.networkmodule.apiData.UserMedicationResult


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMedicationSwapItem(
    userMedication: UserMedicationResult,
    modifier: Modifier = Modifier,
    onRemove: (UserMedicationResult) -> Unit,
    onEdit: (UserMedicationResult) -> Unit,
    onClick: (String) -> Unit,
) {
    val context = LocalContext.current
    val currentItem by rememberUpdatedState(userMedication)
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onEdit(currentItem)
                    Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
                }

                SwipeToDismissBoxValue.EndToStart -> {
                    onRemove(currentItem)
                    Toast.makeText(context, "Item archived", Toast.LENGTH_SHORT).show()
                }

                SwipeToDismissBoxValue.Settled -> return@rememberSwipeToDismissBoxState false
            }
            return@rememberSwipeToDismissBoxState true
        },

        positionalThreshold = { it * .25f }
    )
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = { DismissBackground(dismissState) },
        content = {
            MedicationItem(
                userMedication, onItemClick = onClick
            )
        })
}

