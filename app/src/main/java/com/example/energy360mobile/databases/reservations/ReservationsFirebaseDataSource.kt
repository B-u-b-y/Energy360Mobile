package com.example.energy360mobile.databases.reservations

import com.example.energy360mobile.databases.reservations.ReservationsEntity
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

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
     * Salva o aggiorna una prenotazione sul Cloud.
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