package com.example.energy360mobile.databases.exercises

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class representing an individual exercise in a user's workout plan.
 *
 * @property id Unique identifier for the exercise entry (auto-generated).
 * @property name The name of the exercise (e.g., "Bench Press").
 * @property sets The number of sets to be performed.
 * @property reps The target number of repetitions per set.
 * @property day The day of the week assigned for this exercise.
 * @property isCompleted Boolean indicating if the user has completed the exercise for the day.
 * @property date The timestamp when the exercise was recorded or completed.
 * @property weights Comma-separated string of weights used for each set.
 * @property actualReps Comma-separated string of repetitions actually performed for each set.
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
