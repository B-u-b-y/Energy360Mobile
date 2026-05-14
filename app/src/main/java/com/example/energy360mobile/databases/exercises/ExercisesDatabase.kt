package com.example.energy360mobile.databases.exercises

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The Room database for storing training exercises.
 * Manages the persistence of workout plans and exercise completion history.
 */
@Database(entities = [ExercisesEntity::class], version = 1, exportSchema = true)
abstract class ExercisesDatabase : RoomDatabase() {

    /**
     * Provides access to the [ExercisesDao].
     */
    abstract fun exercisesDao(): ExercisesDao

    companion object {
        @Volatile
        private var INSTANCE: ExercisesDatabase? = null

        /**
         * Returns the singleton instance of [ExercisesDatabase].
         *
         * @param context The application context.
         * @return The [ExercisesDatabase] instance.
         */
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
