package com.example.energy360mobile.databases.reservations

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class representing an exercise in a workout plan.
 */
@Entity(tableName = "reservations")
data class ReservationsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val eventName: String,
    val day: String,
    val date: Long,
    val place: String,
    val isReserved: Boolean = true,
){
    val isPast: Boolean
        get() = date < System.currentTimeMillis()
}
