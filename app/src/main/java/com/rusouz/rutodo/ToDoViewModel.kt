package com.rusouz.rutodo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class SortBy {
    CATEGORY,
    COMPLETED,
    NONE
}

data class AppSettings(
    val isDarkMode: Boolean = false,
    val sortBy: SortBy = SortBy.NONE
)

class ToDoViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ToDoDatabase.getDatabase(application)
    private val toDoDao = database.toDoDao()

    private val _appSettings = MutableStateFlow(AppSettings()) // Use MutableStateFlow
    val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow() // Expose as StateFlow

    private val _sortBy = MutableStateFlow(SortBy.NONE)
    val sortBy: StateFlow<SortBy> = _sortBy.asStateFlow()

    // Combine the sorting flow with the data flow
    val toDos: StateFlow<List<ToDoItem>> = _sortBy.flatMapLatest { sort ->
        when (sort) {
            SortBy.CATEGORY -> toDoDao.getAllToDos() // You might want a specific "sorted by category" query
            SortBy.COMPLETED -> toDoDao.getToDosByCompletion(true) // Show completed
            SortBy.NONE -> toDoDao.getAllToDos()
        }
    }.catch { exception ->
        // Handle exceptions, e.g., log them or show an error message
        emit(emptyList()) // Emit an empty list on error
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())



    fun addToDo(title: String, description: String, category: String) {
        viewModelScope.launch {
            val newToDo = ToDoItem(title = title, description = description, category = category)
            toDoDao.insertToDo(newToDo)
        }
    }

    fun updateToDo(updatedToDo: ToDoItem) {
        viewModelScope.launch {
            toDoDao.updateToDo(updatedToDo)
        }
    }

    fun deleteToDo(id: UUID) {
        viewModelScope.launch {
            toDoDao.deleteToDoById(id)
        }
    }

    fun updateSettings(newSettings: AppSettings) {
        _appSettings.value = newSettings
        _sortBy.value = newSettings.sortBy  // Update sort order when settings change
    }
}