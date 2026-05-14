package com.example.energy360mobile.databases.steps

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The Room database for storing step counting information.
 * It follows the singleton pattern to ensure only one instance of the database exists.
 */
@Database(entities = [StepsEntity::class], version = 1)
abstract class StepsDatabase : RoomDatabase() {

    /**
     * Provides access to the [StepsDao].
     */
    abstract fun stepsDao(): StepsDao

    companion object {
        @Volatile
        private var INSTANCE: StepsDatabase? = null

        /**
         * Returns the singleton instance of [StepsDatabase].
         *
         * @param context The application context.
         * @return The [StepsDatabase] instance.
         */
        fun getDatabase(context: Context): StepsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StepsDatabase::class.java,
                    "steps_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}