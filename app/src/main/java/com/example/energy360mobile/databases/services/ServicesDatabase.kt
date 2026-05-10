package com.example.energy360mobile.databases.services

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ServicesEntity::class], version = 1, exportSchema = true)
abstract class ServicesDatabase : RoomDatabase() {

    abstract fun servicesDao(): ServicesDao

    private class ServicesDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val dao = database.servicesDao()
                    seedDatabase(dao)
                }
            }
        }

        suspend fun seedDatabase(dao: ServicesDao) {
            val now = System.currentTimeMillis()
            val dayMillis = 24 * 60 * 60 * 1000L
            val hourMillis = 60 * 60 * 1000L

            val services = listOf(
                ServicesEntity(
                    id = 0,
                    name = "Yoga",
                    description = "Corso di rilassamento e flessibilità per corpo e mente.",
                    category = "Benessere",
                    duration = "60 min",
                    date = now + dayMillis + (18 * hourMillis),
                    place = "Sala A"
                ),
                ServicesEntity(
                    id = 0,
                    name = "CrossFit",
                    description = "Allenamento funzionale ad alta intensità per sfidare i tuoi limiti.",
                    category = "Funzionale",
                    duration = "45 min",
                    date = now + (2 * dayMillis) + (19 * hourMillis),
                    place = "Area Cross"
                ),
                ServicesEntity(
                    id = 0,
                    name = "Pilates",
                    description = "Esercizi mirati alla tonificazione muscolare e al miglioramento della postura.",
                    category = "Benessere",
                    duration = "50 min",
                    date = now + (3 * dayMillis) + (17 * hourMillis),
                    place = "Sala B"
                ),
                ServicesEntity(
                    id = 0,
                    name = "Spinning",
                    description = "Allenamento cardio intenso su bike stazionaria a ritmo di musica.",
                    category = "Cardio",
                    duration = "45 min",
                    date = now + (4 * dayMillis) + (20 * hourMillis),
                    place = "Sala Bike"
                ),
                ServicesEntity(
                    id = 0,
                    name = "Zumba",
                    description = "Divertente combinazione di fitness e danza su ritmi latino-americani.",
                    category = "Cardio",
                    duration = "60 min",
                    date = now + (5 * dayMillis) + (18 * hourMillis),
                    place = "Sala Grande"
                ),
                ServicesEntity(
                    id = 0,
                    name = "Functional Training",
                    description = "Allenamento completo che simula i movimenti naturali del corpo.",
                    category = "Funzionale",
                    duration = "50 min",
                    date = now + (6 * dayMillis) + (16 * hourMillis),
                    place = "Area Funzionale"
                ),
                ServicesEntity(
                    id = 0,
                    name = "Kickboxing",
                    description = "Tecniche di combattimento unite a un intenso lavoro aerobico.",
                    category = "Combattimento",
                    duration = "60 min",
                    date = now + (7 * dayMillis) + (21 * hourMillis),
                    place = "Sala Combat"
                ),
                ServicesEntity(
                    id = 0,
                    name = "Calisthenics",
                    description = "Allenamento di forza e coordinazione basato su esercizi a corpo libero.",
                    category = "Forza",
                    duration = "60 min",
                    date = now + (3 * dayMillis) + (15 * hourMillis),
                    place = "Area Calisthenics"
                ),
                ServicesEntity(
                    id = 0,
                    name = "Stretching",
                    description = "Sessione dedicata all'allungamento muscolare e al recupero post-allenamento.",
                    category = "Recupero",
                    duration = "30 min",
                    date = now + (2 * dayMillis) + (14 * hourMillis),
                    place = "Sala Relax"
                ),
                ServicesEntity(
                    id = 0,
                    name = "Body Pump",
                    description = "Allenamento con bilancieri per migliorare forza e resistenza.",
                    category = "Forza",
                    duration = "55 min",
                    date = now + (4 * dayMillis) + (18 * hourMillis),
                    place = "Sala Pesi"
                ),
                ServicesEntity(
                    id = 0,
                    name = "HIIT",
                    description = "Interval training ad alta intensità per bruciare calorie rapidamente.",
                    category = "Cardio",
                    duration = "35 min",
                    date = now + (6 * dayMillis) + (19 * hourMillis),
                    place = "Studio 2"
                ),
                ServicesEntity(
                    id = 0,
                    name = "Meditazione Guidata",
                    description = "Tecniche di respirazione e rilassamento mentale.",
                    category = "Benessere",
                    duration = "40 min",
                    date = now + dayMillis + (20 * hourMillis),
                    place = "Sala Zen"
                )
            )

            services.forEach { dao.insert(it) }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ServicesDatabase? = null

        fun getDatabase(context: Context): ServicesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ServicesDatabase::class.java,
                    "services_database"
                )
                .addCallback(ServicesDatabaseCallback(CoroutineScope(Dispatchers.IO)))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}