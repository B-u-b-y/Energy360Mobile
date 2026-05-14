package com.example.energy360mobile.databases.userData

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The Room database for storing user information.
 * It follows the singleton pattern to ensure only one instance of the database exists.
 */
@Database(entities = [UserEntity::class], version = 1, exportSchema = true)
abstract class UserDatabase : RoomDatabase() {

    /**
     * Provides access to the [UserDao].
     */
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        /**
         * Returns the singleton instance of [UserDatabase].
         *
         * @param context The application context.
         * @return The [UserDatabase] instance.
         */
        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}