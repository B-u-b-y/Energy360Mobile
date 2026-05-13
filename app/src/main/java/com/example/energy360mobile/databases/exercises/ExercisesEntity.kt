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
    var name: String = "",
    var sets: Int = 0,
    var reps: String = "",
    var day: String = "",
    var isCompleted: Boolean = false,
    var date: Long = System.currentTimeMillis(),
    var weights: String = "",
    var actualReps: String = ""
)
