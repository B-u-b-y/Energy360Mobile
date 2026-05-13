package com.example.energy360mobile.databases.exercises

import com.example.energy360mobile.databases.exercises.ExercisesEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ExercisesFirebaseDataSource {

    // Inizializza l'istanza del database Firestore
    private val db = FirebaseFirestore.getInstance()

    // Il nome della "tabella" (collezione) su Firebase
    private val collectionName = "exercises"

    /**
     * Scarica tutti gli esercizi dal Cloud.
     * Usiamo 'suspend' perché è un'operazione di rete che richiede tempo.
     */
    suspend fun fetchAllExercises(): List<ExercisesEntity> {
        return try {
            // Chiamata di rete: prende tutti i documenti e attende il risultato con await()
            val snapshot = db.collection(collectionName).get().await()
            snapshot.toObjects(ExercisesEntity::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // Se salta la connessione, ritorniamo una lista vuota
        }
    }

    /**
     * Salva o aggiorna un esercizio sul Cloud.
     */
    suspend fun saveExercise(exercise: ExercisesEntity) {
        try {
            // Usiamo l'ID del tuo esercizio come ID del documento su Firebase.
            // Se esiste già lo sovrascrive (aggiorna), altrimenti lo crea.
            db.collection(collectionName)
                .document(exercise.id.toString())
                .set(exercise)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}