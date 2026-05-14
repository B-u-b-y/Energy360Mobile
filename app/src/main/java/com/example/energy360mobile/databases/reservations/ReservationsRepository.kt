package com.example.energy360mobile.databases.reservations

import kotlinx.coroutines.flow.Flow

/**
 * Repository class that abstracts access to reservation data from both local (Room)
 * and remote (Firebase) data sources.
 *
 * @property dao The [ReservationsDao] for local database operations.
 * @property firebaseDataSource The [ReservationsFirebaseDataSource] for remote database operations.
 */
class ReservationsRepository(private val dao: ReservationsDao, private val firebaseDataSource: ReservationsFirebaseDataSource) {

    /**
     * A [Flow] emitting the list of all reservations from the local database.
     */
    val allReservations: Flow<List<ReservationsEntity>> = dao.getAllReservations()

    /**
     * Retrieves reservations filtered by a specific day.
     *
     * @param day The day string to filter by.
     * @return A [Flow] of filtered [ReservationsEntity] objects.
     */
    fun getReservationsByDay(day: String): Flow<List<ReservationsEntity>> {
        return dao.getReservationsByDay(day)
    }

    /**
     * Synchronizes reservations from Firebase to the local Room database.
     */
    suspend fun syncReservationsFromCloud() {
        try {
            val remoteReservations = firebaseDataSource.fetchAllReservations()
            if (remoteReservations.isNotEmpty()) {
                remoteReservations.forEach { dao.insert(it) }
            }
        } catch (e: Exception) {
            // Error handling (e.g., if user is offline, use cached Room data)
        }
    }

    /**
     * Inserts a new reservation, saving it both locally and on the cloud after checking for duplicates.
     *
     * @param reservation The [ReservationsEntity] to insert.
     */
    suspend fun insertReservations(reservation: ReservationsEntity) {
        val existsReservation: Boolean = dao.isAlreadyReserved(reservation.eventName, reservation.date)
        val existsReservationFirebase: Boolean = firebaseDataSource.checkReservation(reservation)
         if (!existsReservation && !existsReservationFirebase) {
            val reservationFirebaseId = firebaseDataSource.saveReservation(reservation)
            reservation.id = reservationFirebaseId?.id.toString()
            dao.insert(reservation)
        }
    }

    /**
     * Deletes a reservation from both the local database and the cloud.
     *
     * @param reservation The [ReservationsEntity] to remove.
     */
    suspend fun deleteReservation(reservation: ReservationsEntity) {
        dao.delete(reservation)
        firebaseDataSource.deleteReservation(reservation.id)
    }

    /**
     * Deletes all reservations from the local database.
     */
    suspend fun deleteAll() {
        dao.deleteAll()
    }
}