package com.example.energy360mobile.databases.exercises

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class representing an exercise in a workout plan.
 */
@Entity(tableName = "exercises")
data class ExercisesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val sets: Int,
    val reps: String,
    val day: String,
    val isCompleted: Boolean = false,
    val date: Long = System.currentTimeMillis(),
    val weights: String = "",
    val actualReps: String = ""
)
