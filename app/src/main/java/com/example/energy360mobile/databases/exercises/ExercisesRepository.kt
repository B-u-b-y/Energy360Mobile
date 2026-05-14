package com.example.energy360mobile.databases.exercises

import kotlinx.coroutines.flow.Flow

/**
 * Repository class that abstracts access to exercise data from both local (Room)
 * and remote (Firebase) data sources.
 *
 * @property dao The [ExercisesDao] for local database operations.
 * @property firebaseDataSource The [ExercisesFirebaseDataSource] for cloud-based exercise management.
 */
class ExercisesRepository(private val dao: ExercisesDao, private val firebaseDataSource: ExercisesFirebaseDataSource) {

    /**
     * A [Flow] emitting the list of all exercises currently in the local database.
     */
    val allExercises: Flow<List<ExercisesEntity>> = dao.getAllExercises()

    /**
     * Retrieves a flow of exercises filtered by the day of the week.
     *
     * @param day The day string to filter by.
     * @return A [Flow] of [ExercisesEntity] objects for that day.
     */
    fun getExercisesByDay(day: String): Flow<List<ExercisesEntity>> {
        return dao.getExercisesByDay(day)
    }

    /**
     * A [Flow] emitting the list of exercises that have been marked as completed.
     */
    val completedExercises: Flow<List<ExercisesEntity>> = dao.getCompletedExercises()

    /**
     * Updates the completion status, weights, and reps of an exercise in the local database.
     */
    suspend fun updateExerciseCompletion(id: Int, completed: Boolean, weights: String, actualReps: String) {
        dao.updateExerciseCompletion(id, completed, weights, actualReps)
    }

    /**
     * Fetches a list of available exercises from the cloud source.
     *
     * @return A list of [ExercisesEntity] objects from the remote database.
     */
    suspend fun getAvailableExercisesFromCloud(): List<ExercisesEntity> {
        return firebaseDataSource.fetchAllExercises()
    }

    /**
     * Synchronizes exercise data from the cloud to the local Room database.
     */
    suspend fun syncExercisesFromCloud() {
        try {
            val remoteExercises = firebaseDataSource.fetchAllExercises()
            if (remoteExercises.isNotEmpty()) {
                remoteExercises.forEach { dao.insert(it) }
            }
        } catch (e: Exception) {
            // Error handling (e.g., fallback to cached data)
        }
    }

    /**
     * Inserts an exercise entry specifically into the local Room database for the current session.
     *
     * @param exercise The [ExercisesEntity] to insert.
     */
    suspend fun insertExercise(exercise: ExercisesEntity) {
        dao.insert(exercise)
    }

    /**
     * Deletes all exercise records from the local database on the current device.
     */
    suspend fun deleteAll() {
        dao.deleteAll()
    }
}