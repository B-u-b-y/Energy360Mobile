package com.example.energy360mobile.databases.reservations

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the reservations table.
 * Provides methods for interacting with the local reservation database using Room.
 */
@Dao
interface ReservationsDao {

    /**
     * Inserts a reservation into the database. If it already exists, it will be replaced.
     *
     * @param reservations The [ReservationsEntity] to be inserted.
     * @return The row ID of the newly inserted item.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reservations : ReservationsEntity): Long

    /**
     * Deletes a specific reservation from the local database.
     *
     * @param reservation The [ReservationsEntity] to be deleted.
     */
    @Delete
    suspend fun delete(reservation: ReservationsEntity)

    /**
     * Retrieves all reservations stored in the local database.
     *
     * @return A [Flow] emitting the list of all [ReservationsEntity] objects.
     */
    @Query("SELECT * FROM reservations")
    fun getAllReservations(): Flow<List<ReservationsEntity>>

    /**
     * Retrieves reservations for a specific day.
     *
     * @param day The day string (e.g., "Monday") to filter by.
     * @return A [Flow] emitting the list of [ReservationsEntity] objects for that day.
     */
    @Query("SELECT * FROM reservations WHERE day = :day")
    fun getReservationsByDay(day: String): Flow<List<ReservationsEntity>>

    /**
     * Deletes all reservations from the local database.
     */
    @Query("DELETE FROM reservations")
    suspend fun deleteAll()

    /**
     * Checks if a specific event is already reserved for a given date.
     *
     * @param eventName The name of the event.
     * @param date The timestamp of the event.
     * @return True if a reservation already exists, false otherwise.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM reservations WHERE eventName = :eventName AND date = :date)")
    suspend fun isAlreadyReserved(eventName: String, date: Long): Boolean
}
