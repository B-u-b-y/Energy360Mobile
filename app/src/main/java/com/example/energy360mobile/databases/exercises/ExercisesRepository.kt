package com.example.energy360mobile.databases.exercises

import kotlinx.coroutines.flow.Flow

class ExercisesRepository(private val dao: ExercisesDao) {

    val allExercises: Flow<List<ExercisesEntity>> = dao.getAllExercises()

    fun getExercisesByDay(day: String): Flow<List<ExercisesEntity>> {
        return dao.getExercisesByDay(day)
    }

    suspend fun insertExercise(exercise: ExercisesEntity) {
        dao.insert(exercise)
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }
}
