package com.example.energy360mobile.databases.exercises

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ExercisesEntity::class], version = 1, exportSchema = true)
abstract class ExercisesDatabase : RoomDatabase() {

    abstract fun exercisesDao(): ExercisesDao

    companion object {
        @Volatile
        private var INSTANCE: ExercisesDatabase? = null

        fun getDatabase(context: Context): ExercisesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExercisesDatabase::class.java,
                    "exercises_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
