package com.example.energy360mobile.databases.reservations

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class representing a reservation for a fitness event or course.
 *
 * @property id The unique identifier of the reservation (typically from Firebase).
 * @property eventName The name of the booked event.
 * @property userId The ID of the user who made the reservation.
 * @property day The day of the week when the event takes place.
 * @property date The timestamp in milliseconds of the event.
 * @property place The location where the event is held.
 * @property isReserved Boolean flag indicating if the reservation is currently active.
 */
@Entity(tableName = "reservations")
data class ReservationsEntity(
    @PrimaryKey
    var id: String,
    val eventName: String,
    val userId: String?,
    val day: String,
    val date: Long,
    val place: String,
    val isReserved: Boolean = true,
){
    /**
     * Checks if the event's date has already passed.
     */
    val isPast: Boolean
        get() = date < System.currentTimeMillis()
}
