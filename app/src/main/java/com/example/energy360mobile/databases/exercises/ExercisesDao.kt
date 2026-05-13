package com.example.energy360mobile.databases.exercises;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExercisesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercises : ExercisesEntity)

    @Query("SELECT * FROM exercises")
    fun getAllExercises(): Flow<List<ExercisesEntity>>

    @Query("SELECT * FROM exercises WHERE day = :day")
    fun getExercisesByDay(day: String): Flow<List<ExercisesEntity>>

    @Query("SELECT * FROM exercises WHERE isCompleted = 1 ORDER BY date DESC")
    fun getCompletedExercises(): Flow<List<ExercisesEntity>>

    @Query("DELETE FROM exercises")
    suspend fun deleteAll()

    @Query("UPDATE exercises SET isCompleted = :completed, weights = :weights, actualReps = :actualReps WHERE id = :id")
    suspend fun updateExerciseCompletion(id: Int, completed: Boolean, weights: String, actualReps: String)
}
