package com.example.energy360mobile.databases.reservations;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reservations : ReservationsEntity): Long

    @Delete
    suspend fun delete(reservation: ReservationsEntity)

    @Query("SELECT * FROM reservations")
    fun getAllReservations(): Flow<List<ReservationsEntity>>

    @Query("SELECT * FROM reservations WHERE day = :day")
    fun getReservationsByDay(day: String): Flow<List<ReservationsEntity>>

    @Query("DELETE FROM reservations")
    suspend fun deleteAll()

    @Query("SELECT EXISTS(SELECT 1 FROM reservations WHERE eventName = :eventName AND date = :date)")
    suspend fun isAlreadyReserved(eventName: String, date: Long): Boolean
}
