package com.rusouz.rutodo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentSettings: AppSettings,
    onSettingsChanged: (AppSettings) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        content = { innerPadding ->
            SettingsContent(
                currentSettings = currentSettings,
                onSettingsChanged = onSettingsChanged,
                modifier = Modifier.padding(innerPadding)
            )
        }
    )
}

@Composable
fun SettingsContent(
    currentSettings: AppSettings,
    onSettingsChanged: (AppSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DarkModeSetting(
            isDarkMode = currentSettings.isDarkMode,
            onDarkModeChanged = { isDarkMode ->
                onSettingsChanged(currentSettings.copy(isDarkMode = isDarkMode))
            }
        )

        SortBySetting(
            sortBy = currentSettings.sortBy,
            onSortByChanged = { sortBy ->
                onSettingsChanged(currentSettings.copy(sortBy = sortBy))
            }
        )
    }
}

@Composable
fun DarkModeSetting(isDarkMode: Boolean, onDarkModeChanged: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Dark Mode", modifier = Modifier.weight(1f))
        Switch(
            checked = isDarkMode,
            onCheckedChange = onDarkModeChanged
        )
    }
    Divider()
}

@Composable
fun SortBySetting(sortBy: SortBy, onSortByChanged: (SortBy) -> Unit) {
    val sortOptions = SortBy.values()
    var expanded by remember { mutableStateOf(false) }

    Text("Sort By", style = MaterialTheme.typography.titleMedium)

    Column {
        OutlinedTextField(
            value = sortBy.name,
            onValueChange = { },
            readOnly = true,
            label = { Text("Sort By") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expand Sort Options"
                    )
                }
            }
        )

        if (expanded) {
            sortOptions.forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = sortBy == option,
                        onClick = {
                            onSortByChanged(option)
                            expanded = false
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(text = option.name, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}