package com.example.energy360mobile.databases.reservations

import com.example.energy360mobile.databases.reservations.ReservationsFirebaseDataSource
import kotlinx.coroutines.flow.Flow

class ReservationsRepository(private val dao: ReservationsDao,private val firebaseDataSource: ReservationsFirebaseDataSource) {

    val allReservations: Flow<List<ReservationsEntity>> = dao.getAllReservations()

    fun getReservationsByDay(day: String): Flow<List<ReservationsEntity>> {
        return dao.getReservationsByDay(day)
    }

    suspend fun syncReservationsFromCloud() {
        try {
            val remoteReservations = firebaseDataSource.fetchAllReservations()
            if (remoteReservations.isNotEmpty()) {
                remoteReservations.forEach { dao.insert(it) }
            }
        } catch (e: Exception) {
            // Gestione dell'errore (es. se l'utente è offline, usa i vecchi dati in Room)
        }
    }

    suspend fun insertReservations(reservation: ReservationsEntity) {
        val generatedId = dao.insert(reservation)
        val reservationWithRealId = reservation.copy(id = generatedId.toInt())
        firebaseDataSource.saveReservation(reservationWithRealId)
    }

    suspend fun deleteReservation(reservation: ReservationsEntity) {
        dao.delete(reservation)
        firebaseDataSource.deleteReservation(reservation.id.toString())
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }
}

