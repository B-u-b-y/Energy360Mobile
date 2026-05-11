package com.example.energy360mobile.databases.exercises

import kotlinx.coroutines.flow.Flow
import com.example.energy360mobile.databases.exercises.ExercisesFirebaseDataSource
import kotlin.math.log

//firebaseDatasoure è la dipendenza da cloude
class ExercisesRepository(private val dao: ExercisesDao, private val firebaseDataSource: ExercisesFirebaseDataSource) {

    val allExercises: Flow<List<ExercisesEntity>> = dao.getAllExercises()

    fun getExercisesByDay(day: String): Flow<List<ExercisesEntity>> {
        return dao.getExercisesByDay(day)
    }

    // funzione di sincronizzazione
    // Questa verrà chiamata all'apertura dell'app o con un refresh
    suspend fun syncExercisesFromCloud() {
        try {
            val remoteExercises = firebaseDataSource.fetchAllExercises()
            if (remoteExercises.isNotEmpty()) {
                // Inseriamo i dati freschi nel database locale
                remoteExercises.forEach { dao.insert(it) }
            }
        } catch (e: Exception) {
        }
    }

    // Salva sia in locale che sul cloud
    suspend fun insertExercise(exercise: ExercisesEntity) {
        dao.insert(exercise)
        firebaseDataSource.saveExercise(exercise) // Invia a Firebase in background
    }

    suspend fun deleteAll() {
        dao.deleteAll()
       //Vengono eliminati tutti quanti dal dispositivo CORRENTE, altrimenti và creata una funzione deleteAll sul datasource, però insensato in questo caso
    }
}

