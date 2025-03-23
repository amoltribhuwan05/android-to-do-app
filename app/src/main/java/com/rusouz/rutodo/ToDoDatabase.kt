package com.rusouz.rutodo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rusouz.rutodo.ToDoDao  // Correct import
import com.rusouz.rutodo.ToDoItem

@Database(entities = [ToDoItem::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class) // Add this line
abstract class ToDoDatabase : RoomDatabase() {

    abstract fun toDoDao(): ToDoDao

    companion object {
        @Volatile
        private var INSTANCE: ToDoDatabase? = null

        fun getDatabase(context: Context): ToDoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToDoDatabase::class.java,
                    "todo_database"
                )
                    .fallbackToDestructiveMigration() // THIS IS FOR DEVELOPMENT ONLY.  Removes data on schema changes.
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}