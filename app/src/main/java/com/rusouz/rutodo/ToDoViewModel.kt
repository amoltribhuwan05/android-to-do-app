package com.rusouz.rutodo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rusouz.rutodo.db.ToDoDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortBy {
    CATEGORY,
    COMPLETED,
    NONE
}

data class AppSettings(
    var isDarkMode: Boolean = false,
    var sortBy: SortBy = SortBy.NONE
)

class ToDoViewModel(application: Application) :
    AndroidViewModel(application) {

    private val database = ToDoDatabase.getDatabase(application)
    private val toDoDao = database.toDoDao()
    private val settingsDao = database.settingsDao()

    private val _appSettings = MutableStateFlow(AppSettings())
    val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow()

    private val _sortBy = MutableStateFlow(SortBy.NONE)
    val sortBy: StateFlow<SortBy> = _sortBy.asStateFlow()

    val toDos: StateFlow<List<ToDoItem>> = _sortBy.flatMapLatest { sort ->
        toDoDao.getAllToDos(
            sortBy = when (sort) {
                SortBy.CATEGORY -> "CATEGORY"
                SortBy.COMPLETED -> "COMPLETED"
                SortBy.NONE -> "NONE"
            }
        )
    }.catch { exception ->
        emit(emptyList())
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val settingsEntity = settingsDao.getSettings().firstOrNull()

            _appSettings.value = if (settingsEntity != null) {
                AppSettings(
                    isDarkMode = settingsEntity.isDarkMode,
                    sortBy = try {
                        SortBy.valueOf(settingsEntity.sortBy)
                    } catch (e: IllegalArgumentException) {
                        SortBy.NONE
                    }
                )
            } else {
                AppSettings()
            }
            _sortBy.value = _appSettings.value.sortBy
        }
    }

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

    fun deleteToDo(toDoItem: ToDoItem) {
        viewModelScope.launch {
            toDoDao.deleteToDoById(toDoItem.id)
        }
    }

    fun updateSettings(newSettings: AppSettings) {
        viewModelScope.launch {
            _appSettings.value = newSettings
            _sortBy.value = newSettings.sortBy
            settingsDao.insertSettings(
                SettingsEntity(
                    id = 1,
                    isDarkMode = newSettings.isDarkMode,
                    sortBy = newSettings.sortBy.name
                )
            )
        }
    }
}