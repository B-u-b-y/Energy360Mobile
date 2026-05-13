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

    val completedExercises: Flow<List<ExercisesEntity>> = dao.getCompletedExercises()

    suspend fun updateExerciseCompletion(id: Int, completed: Boolean, weights: String, actualReps: String) {
        dao.updateExerciseCompletion(id, completed, weights, actualReps)
    }

    suspend fun getAvailableExercisesFromCloud(): List<ExercisesEntity> {
        return firebaseDataSource.fetchAllExercises()
    }

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

    // Salva solo nel database locale (Room) per la sessione corrente
    suspend fun insertExercise(exercise: ExercisesEntity) {
        dao.insert(exercise)
    }

    suspend fun deleteAll() {
        dao.deleteAll()
       //Vengono eliminati tutti quanti dal dispositivo CORRENTE, altrimenti và creata una funzione deleteAll sul datasource, però insensato in questo caso
    }
}

