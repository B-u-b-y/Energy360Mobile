package com.example.energy360mobile.databases.exercises

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the exercises table.
 * Provides methods for interacting with the local training database using Room.
 */
@Dao
interface ExercisesDao {

    /**
     * Inserts an exercise into the database. If it already exists, it will be replaced.
     *
     * @param exercises The [ExercisesEntity] to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercises : ExercisesEntity)

    /**
     * Retrieves all exercises stored in the database.
     *
     * @return A [Flow] emitting the list of all [ExercisesEntity] objects.
     */
    @Query("SELECT * FROM exercises")
    fun getAllExercises(): Flow<List<ExercisesEntity>>

    /**
     * Retrieves exercises scheduled for a specific day.
     *
     * @param day The day of the week to filter by.
     * @return A [Flow] emitting the list of [ExercisesEntity] for that day.
     */
    @Query("SELECT * FROM exercises WHERE day = :day")
    fun getExercisesByDay(day: String): Flow<List<ExercisesEntity>>

    /**
     * Retrieves all exercises marked as completed, ordered by most recent.
     *
     * @return A [Flow] emitting the list of completed [ExercisesEntity] objects.
     */
    @Query("SELECT * FROM exercises WHERE isCompleted = 1 ORDER BY date DESC")
    fun getCompletedExercises(): Flow<List<ExercisesEntity>>

    /**
     * Deletes all exercises from the local database.
     */
    @Query("DELETE FROM exercises")
    suspend fun deleteAll()

    /**
     * Updates the completion status and performance metrics of a specific exercise.
     *
     * @param id The ID of the exercise to update.
     * @param completed The new completion status.
     * @param weights The string representation of weights used.
     * @param actualReps The string representation of reps performed.
     */
    @Query("UPDATE exercises SET isCompleted = :completed, weights = :weights, actualReps = :actualReps WHERE id = :id")
    suspend fun updateExerciseCompletion(id: Int, completed: Boolean, weights: String, actualReps: String)
}