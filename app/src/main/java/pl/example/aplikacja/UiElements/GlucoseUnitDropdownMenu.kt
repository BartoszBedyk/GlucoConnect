package pl.example.aplikacja.UiElements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.example.aplikacja.mappters.formatDiabetesType
import pl.example.aplikacja.mappters.formatUnit
import pl.example.aplikacja.mappters.formatUserType
import pl.example.networkmodule.apiData.enumTypes.DiabetesType
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiData.enumTypes.RestrictedUserType


@Composable
fun GlucoseUnitDropdownMenu(
    selectedUnit: GlucoseUnitType,
    onUnitSelected: (GlucoseUnitType) -> Unit,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }
    var paddingValue = 8.dp

    if (label.isBlank()) paddingValue = 0.dp

    Row(Modifier.padding(vertical = paddingValue)) {
        OutlinedTextField(
            value = formatUnit(selectedUnit),
            onValueChange = {},
            readOnly = true,
            label = { Text(text = label, fontSize = 18.sp) },
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
                    text = {
                        Text(
                            text = formatUnit(unit),
                            fontSize = 16.sp
                        )
                    },
                    onClick = {
                        onUnitSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun UserTypeDropdownMenu(
    selectedUnit: RestrictedUserType,
    onUnitSelected: (RestrictedUserType) -> Unit,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }
    var paddingValue = 8.dp

    if (label.isBlank()) paddingValue = 0.dp

    Row(Modifier.padding(vertical = paddingValue)) {
        OutlinedTextField(
            value = formatUserType(selectedUnit),
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
            RestrictedUserType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(text = formatUserType(type)) },
                    onClick = {
                        onUnitSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun DiabetesTypeDropdownMenu(
    selectedDiabetesType: DiabetesType,
    onTypeSelected: (DiabetesType) -> Unit,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }
    var paddingValue = 8.dp

    if (label.isBlank()) paddingValue = 0.dp

    Row(Modifier.padding(vertical = paddingValue)) {
        OutlinedTextField(
            value = formatDiabetesType(selectedDiabetesType),
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
            DiabetesType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(text = formatDiabetesType(type)) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}