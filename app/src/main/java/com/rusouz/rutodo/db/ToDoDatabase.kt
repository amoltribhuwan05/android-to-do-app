package com.rusouz.rutodo.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rusouz.rutodo.Converters
import com.rusouz.rutodo.SettingsEntity
import com.rusouz.rutodo.db.ToDoDao
import com.rusouz.rutodo.ToDoItem

@Database(entities = [ToDoItem::class, SettingsEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ToDoDatabase : RoomDatabase() {

    abstract fun toDoDao(): ToDoDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: ToDoDatabase? = null

        fun getDatabase(context: Context): ToDoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToDoDatabase::class.java,
                    "todo_database" // Database name
                )
                    .fallbackToDestructiveMigration() // FOR DEVELOPMENT ONLY
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}