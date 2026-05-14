package com.example.energy360mobile.databases.steps

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class representing a step count record in the local database.
 *
 * @property id The unique identifier of the step record (auto-generated).
 * @property timeInMsecs The timestamp in milliseconds when the step count was recorded.
 * @property steps The number of steps recorded at that specific time.
 */
@Entity(tableName = "steps")
data class StepsEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timeInMsecs: Long,
    val steps: Long
)