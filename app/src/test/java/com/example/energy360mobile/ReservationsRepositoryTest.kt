package com.example.energy360mobile

import com.example.energy360mobile.databases.reservations.ReservationsDao
import com.example.energy360mobile.databases.reservations.ReservationsEntity
import com.example.energy360mobile.databases.reservations.ReservationsFirebaseDataSource
import com.example.energy360mobile.databases.reservations.ReservationsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Unit tests for [ReservationsRepository].
 * Verifies that the repository correctly coordinates data between the local DAO
 * and the remote Firebase data source.
 */
class ReservationsRepositoryTest {

    private val dao = mockk<ReservationsDao>(relaxed = true)
    private val firebase = mockk<ReservationsFirebaseDataSource>(relaxed = true)
    private val repository = ReservationsRepository(dao, firebase)

    /**
     * Verifies that [ReservationsRepository.syncReservationsFromCloud] fetches data from
     * Firebase and inserts all received reservations into the local database.
     */
    @Test
    fun syncReservationsFromCloud_insertsRemoteReservations() = runBlocking {
        val remoteReservations = listOf(
            ReservationsEntity(id = "1", eventName = "Yoga", userId = "u1", day = "Mon", date = 1000L, place = "Sala A"),
            ReservationsEntity(id = "2", eventName = "CrossFit", userId = "u1", day = "Tue", date = 2000L, place = "Area Cross")
        )
        coEvery { firebase.fetchAllReservations() } returns remoteReservations

        repository.syncReservationsFromCloud()

        coVerify(exactly = 2) { dao.insert(any()) }
    }

    /**
     * Verifies that [ReservationsRepository.insertReservations] correctly checks for
     * existing reservations and, if none are found, saves to both Firebase and DAO.
     */
    @Test
    fun insertReservations_whenNotExists_savesToFirebaseAndDao() = runBlocking {
        val reservation = ReservationsEntity(id = "", eventName = "Yoga", userId = "u1", day = "Mon", date = 1000L, place = "Sala A")
        
        // Mock checks: reservation does not exist locally or on firebase
        coEvery { dao.isAlreadyReserved(any(), any()) } returns false
        coEvery { firebase.checkReservation(any()) } returns false
        
        // Mock firebase save returning a reference with a new ID
        val mockRef = mockk<com.google.firebase.firestore.DocumentReference>()
        coEvery { mockRef.id } returns "new_id_from_firebase"
        coEvery { firebase.saveReservation(any()) } returns mockRef

        repository.insertReservations(reservation)

        coVerify { firebase.saveReservation(any()) }
        coVerify { dao.insert(match { it.id == "new_id_from_firebase" }) }
    }

    /**
     * Verifies that [ReservationsRepository.insertReservations] aborts the insertion
     * if the reservation already exists in the local database.
     */
    @Test
    fun insertReservations_whenExistsLocally_doesNotSave() = runBlocking {
        val reservation = ReservationsEntity(id = "", eventName = "Yoga", userId = "u1", day = "Mon", date = 1000L, place = "Sala A")
        
        coEvery { dao.isAlreadyReserved(any(), any()) } returns true

        repository.insertReservations(reservation)

        coVerify(exactly = 0) { firebase.saveReservation(any()) }
        coVerify(exactly = 0) { dao.insert(any()) }
    }

    /**
     * Verifies that [ReservationsRepository.deleteReservation] removes the reservation
     * from both the local DAO and the remote Firebase source.
     */
    @Test
    fun deleteReservation_callsDaoAndDeleteFromFirebase() = runBlocking {
        val reservation = ReservationsEntity(id = "123", eventName = "Yoga", userId = "u1", day = "Mon", date = 1000L, place = "Sala A")

        repository.deleteReservation(reservation)

        coVerify { dao.delete(reservation) }
        coVerify { firebase.deleteReservation("123") }
    }

    /**
     * Verifies that [ReservationsRepository.deleteAll] calls the local DAO to clear all data.
     */
    @Test
    fun deleteAll_callsDaoDeleteAll() = runBlocking {
        repository.deleteAll()
        coVerify { dao.deleteAll() }
    }
}
