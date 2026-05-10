package com.example.energy360mobile.databases.reservations

import kotlinx.coroutines.flow.Flow

class ReservationsRepository(private val dao: ReservationsDao) {

    val allReservations: Flow<List<ReservationsEntity>> = dao.getAllReservations()

    fun getReservationsByDay(day: String): Flow<List<ReservationsEntity>> {
        return dao.getReservationsByDay(day)
    }

    suspend fun insertReservations(reservation: ReservationsEntity) {
        dao.insert(reservation)
    }

    suspend fun deleteReservation(reservation: ReservationsEntity) {
        dao.delete(reservation)
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }
}
