package pl.example.aplikacja.UiElements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.example.aplikacja.formatUnit
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType


@Composable
fun GlucoseUnitDropdownMenu(
    selectedUnit: GlucoseUnitType,
    onUnitSelected: (GlucoseUnitType) -> Unit,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }
    var paddingValue = 8.dp

    if(label.isBlank()) paddingValue = 0.dp

    Row(Modifier.padding(vertical = paddingValue)) {
        OutlinedTextField(
            value = formatUnit(selectedUnit),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Rozwiń",
                    Modifier.clickable { expanded = true }
                )
            },
            modifier = Modifier
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            GlucoseUnitType.entries.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(text = formatUnit(unit)) },
                    onClick = {
                        onUnitSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}