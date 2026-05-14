package com.example.energy360mobile.databases.reservations

import com.example.energy360mobile.databases.reservations.ReservationsEntity
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

/**
 * Data source class responsible for interacting with Google Firebase Firestore
 * to manage fitness reservations in the cloud.
 */
class ReservationsFirebaseDataSource {

    private val db = FirebaseFirestore.getInstance()
    private val collectionName = "reservations"

    /**
     * Fetches all reservations from the cloud data source.
     *
     * @return A list of [ReservationsEntity] objects.
     */
    suspend fun fetchAllReservations(): List<ReservationsEntity> {
        return try {
            val snapshot = db.collection(collectionName).get().await()
            snapshot.toObjects(ReservationsEntity::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Checks if a reservation already exists on the cloud for a specific user, event, and date.
     *
     * @param reservation The reservation data to check.
     * @return True if a matching reservation exists, false otherwise.
     */
    fun checkReservation(reservation: ReservationsEntity): Boolean {
        return try{
            val task = db.collection(collectionName)
                .whereEqualTo("userId", reservation.userId)
                .whereEqualTo("eventName", reservation.eventName)
                .whereEqualTo("date", reservation.date)
                .get()

            val snapshot = Tasks.await(task, 5, TimeUnit.SECONDS)
            if(!snapshot.isEmpty)
                snapshot.documents.first().data != null
            else false
        }catch(e: Exception){false}
    }

    /**
     * Saves or updates a reservation in the cloud.
     *
     * @param reservation The [ReservationsEntity] to be saved.
     * @return A [DocumentReference] to the newly created document, or null if an error occurred.
     */
    suspend fun saveReservation(reservation: ReservationsEntity): DocumentReference? {
        try {
            return db.collection(collectionName)
                .add(reservation)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Deletes a single reservation from the cloud using its unique ID.
     *
     * @param reservationId The unique identifier of the reservation document.
     */
    suspend fun deleteReservation(reservationId: String) {
        try {
            db.collection(collectionName)
                .document(reservationId)
                .delete()
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}