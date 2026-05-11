package com.example.energy360mobile.databases.reservations

import com.example.energy360mobile.databases.reservations.ReservationsEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ReservationsFirebaseDataSource {

    private val db = FirebaseFirestore.getInstance()
    private val collectionName = "reservations"

    /**
     * Scarica tutte le prenotazioni dal Cloud.
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
     * Salva o aggiorna una prenotazione sul Cloud.
     */
    suspend fun saveReservation(reservation: ReservationsEntity) {
        try {
            db.collection(collectionName)
                .document(reservation.id.toString())
                .set(reservation)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Elimina una singola prenotazione dal Cloud tramite il suo ID.
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