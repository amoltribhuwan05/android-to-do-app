package com.rusouz.rutodo

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rusouz.rutodo.ui.theme.RuToDoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: ToDoViewModel = viewModel(factory = ToDoViewModelFactory(application))
            val appSettingsState by viewModel.appSettings.collectAsState()

            RuToDoTheme(darkTheme = appSettingsState.isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: ToDoViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                viewModel = viewModel,
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable("settings") {
            SettingsScreen(
                currentSettings = viewModel.appSettings.collectAsState().value,
                onSettingsChanged = { newSettings ->
                    viewModel.updateSettings(newSettings)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: ToDoViewModel, onNavigateToSettings: () -> Unit) {
    val toDos by viewModel.toDos.collectAsState()
    var dialogState by remember { mutableStateOf<DialogState>(DialogState.Hidden) }


    val topAppBarContainerColor = MaterialTheme.colorScheme.primary
    val topAppBarTitleContentColor = MaterialTheme.colorScheme.onPrimary
    val topAppBarActionIconContentColor = MaterialTheme.colorScheme.onPrimary
    val fabContainerColor = MaterialTheme.colorScheme.primary
    val fabContentColor = MaterialTheme.colorScheme.onPrimary

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RuToDo") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = topAppBarContainerColor,
                    titleContentColor = topAppBarTitleContentColor,
                    actionIconContentColor = topAppBarActionIconContentColor
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { dialogState = DialogState.Add },
                containerColor = fabContainerColor,
                contentColor = fabContentColor
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add ToDo")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(toDos, key = { it.id }) { toDo ->
                ToDoItemCard(
                    toDo = toDo,
                    onToDoChange = { updatedToDo -> viewModel.updateToDo(updatedToDo) },
                    onDeleteToDo = {toDoItem ->  viewModel.deleteToDo(toDoItem) },
                    onEditToDo = { dialogState = DialogState.Edit(toDo) }
                )
            }
        }
    }

    // Show the dialog based on the dialog state
    when (dialogState) {
        is DialogState.Add -> {
            ToDoDialog(
                onDismiss = { dialogState = DialogState.Hidden },
                onSaveToDo = { title, description, category ->
                    viewModel.addToDo(title, description, category)
                    dialogState = DialogState.Hidden
                },
                title = "",
                description = "",
                category = ""
            )
        }
        is DialogState.Edit -> {
            val toDo = (dialogState as DialogState.Edit).toDo
            ToDoDialog(
                onDismiss = { dialogState = DialogState.Hidden },
                onSaveToDo = { title, description, category ->
                    viewModel.updateToDo(toDo.copy(title = title, description = description, category = category))
                    dialogState = DialogState.Hidden
                },
                title = toDo.title,
                description = toDo.description,
                category = toDo.category
            )
        }
        DialogState.Hidden -> Unit
    }
}

@Composable
fun ToDoItemCard(
    toDo: ToDoItem,
    onToDoChange: (ToDoItem) -> Unit,
    onDeleteToDo: (ToDoItem) -> Unit,
    onEditToDo: () -> Unit
) {

    val textColor = if (toDo.isDone) {
        Color.Gray
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onToDoChange(toDo.copy(isDone = !toDo.isDone)) }) {
                Icon(
                    imageVector = if (toDo.isDone) Icons.Filled.CheckCircle else Icons.Default.CheckCircle,
                    contentDescription = if (toDo.isDone) "Mark Undone" else "Mark Done",
                    tint = if (toDo.isDone) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = toDo.title,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = textColor // Use theme-aware text color
                    )
                )
                if (toDo.description.isNotBlank()) {
                    Text(
                        text = toDo.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = textColor
                        )
                    )
                }
                Text(
                    text = toDo.category,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = textColor
                    ))
            }
            IconButton(onClick = { onDeleteToDo(toDo) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete ToDo", tint = Color.Red)
            }
            IconButton(onClick = onEditToDo) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit ToDo", tint = Color.Blue)
            }
        }
    }
}

@Composable
fun ToDoDialog(
    onDismiss: () -> Unit,
    onSaveToDo: (String, String, String) -> Unit,
    title: String,
    description: String,
    category: String
) {
    var titleState by remember { mutableStateOf(title) }
    var descriptionState by remember { mutableStateOf(description) }
    var categoryState by remember { mutableStateOf(category) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (title.isEmpty()) "Add ToDo" else "Edit ToDo") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = titleState,
                    onValueChange = { titleState = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = descriptionState,
                    onValueChange = { descriptionState = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = categoryState,
                    onValueChange = { categoryState = it },
                    label = { Text("Category") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titleState.isNotBlank() && categoryState.isNotBlank()) {
                        onSaveToDo(titleState, descriptionState, categoryState)
                    }
                }
            ) {
                Text(if (title.isEmpty()) "Add" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

sealed class DialogState {
    object Hidden : DialogState()
    object Add : DialogState()
    class Edit(val toDo: ToDoItem) : DialogState()
}

class ToDoViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ToDoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ToDoViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}