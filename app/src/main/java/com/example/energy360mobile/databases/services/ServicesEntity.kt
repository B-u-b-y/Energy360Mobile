package com.example.energy360mobile.databases.services

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class representing a fitness service or course offered by Energy360.
 *
 * @property id The unique identifier of the service (auto-generated).
 * @property name The name of the service/course.
 * @property description A detailed description of what the service offers.
 * @property category The category of the service (e.g., "Yoga", "Crossfit").
 * @property duration The duration of the session as a string (e.g., "60 min").
 * @property date The timestamp in milliseconds when the session is scheduled.
 * @property place The location where the service is held.
 */
@Entity(tableName = "services")
data class ServicesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val category: String,
    val duration: String,
    val date: Long,
    val place: String
)
