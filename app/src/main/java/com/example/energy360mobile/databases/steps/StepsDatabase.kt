package com.example.energy360mobile.databases.steps

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [StepsEntity::class], version = 1)
abstract class StepsDatabase : RoomDatabase() {

    abstract fun stepsDao(): StepsDao

    companion object {
        @Volatile
        private var INSTANCE: StepsDatabase? = null

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