package com.example.energy360mobile.databases.reservations

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(entities = [ReservationsEntity::class], version = 1, exportSchema = true)
abstract class ReservationsDatabase : RoomDatabase() {

    abstract fun reservationsDao(): ReservationsDao

    private class ReservationsDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val dao = database.reservationsDao()
                    seedDatabase(dao)
                }
            }
        }

        suspend fun seedDatabase(dao: ReservationsDao) {
            val calendar = Calendar.getInstance()
            
            val reservations = listOf(
                ReservationsEntity(
                    eventName = "Yoga Flow",
                    day = "Lunedì",
                    date = calendar.apply { set(Calendar.HOUR_OF_DAY, 10); set(Calendar.MINUTE, 0) }.timeInMillis,
                    place = "Sala A",
                    isReserved = true
                ),
                ReservationsEntity(
                    eventName = "CrossFit Challenge",
                    day = "Martedì",
                    date = calendar.apply { set(Calendar.HOUR_OF_DAY, 18); set(Calendar.MINUTE, 30) }.timeInMillis,
                    place = "Area Cross",
                    isReserved = true
                ),
                ReservationsEntity(
                    eventName = "Pilates Posturale",
                    day = "Mercoledì",
                    date = calendar.apply { set(Calendar.HOUR_OF_DAY, 9); set(Calendar.MINUTE, 15) }.timeInMillis,
                    place = "Sala B",
                    isReserved = true
                ),
                ReservationsEntity(
                    eventName = "Total Body",
                    day = "Venerdì",
                    date = calendar.apply { set(Calendar.HOUR_OF_DAY, 17); set(Calendar.MINUTE, 0) }.timeInMillis,
                    place = "Sala Grande",
                    isReserved = true
                )
            )
            
            reservations.forEach { dao.insert(it) }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ReservationsDatabase? = null

        fun getDatabase(context: Context): ReservationsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReservationsDatabase::class.java,
                    "reservations_database"
                )
                .addCallback(ReservationsDatabaseCallback(CoroutineScope(Dispatchers.IO)))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
