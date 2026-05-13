package com.example.energy360mobile.databases.reservations

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
        val existsReservation: Boolean = dao.isAlreadyReserved(reservation.eventName, reservation.date)
        val existsReservationFirebase: Boolean = firebaseDataSource.checkReservation(reservation)
         if (!existsReservation && !existsReservationFirebase) {
            val reservationFirebaseId = firebaseDataSource.saveReservation(reservation)
            reservation.id = reservationFirebaseId?.id.toString()
            dao.insert(reservation)
        }
    }

    suspend fun deleteReservation(reservation: ReservationsEntity) {
        dao.delete(reservation)
        firebaseDataSource.deleteReservation(reservation.id)
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }
}

