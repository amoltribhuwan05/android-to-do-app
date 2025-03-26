package com.rusouz.rutodo.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rusouz.rutodo.ToDoItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface ToDoDao {
    @Query("SELECT * FROM todo_items ORDER BY title ASC") // Example sort order
    fun getAllToDos(): Flow<List<ToDoItem>>

    @Query("SELECT * FROM todo_items WHERE id = :id")
    fun getToDoById(id: UUID): Flow<ToDoItem?>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertToDo(toDoItem: ToDoItem)

    @Update
    suspend fun updateToDo(toDoItem: ToDoItem)

    @Delete
    suspend fun deleteToDo(toDoItem: ToDoItem)

    @Query("DELETE FROM todo_items WHERE id = :id")
    suspend fun deleteToDoById(id: UUID)

    @Query("SELECT * FROM todo_items WHERE category = :category")
    fun getToDosByCategory(category: String): Flow<List<ToDoItem>>

    @Query("SELECT * FROM todo_items WHERE isDone = :isDone")
    fun getToDosByCompletion(isDone: Boolean): Flow<List<ToDoItem>>

    @Query("SELECT * FROM todo_items ORDER BY " +
            "CASE WHEN :sortBy = 'CATEGORY' THEN category " +
            "WHEN :sortBy = 'COMPLETED' THEN CASE WHEN isDone THEN 0 ELSE 1 END " +
            "ELSE title END")
    fun getAllToDos(sortBy: String): Flow<List<ToDoItem>>
}