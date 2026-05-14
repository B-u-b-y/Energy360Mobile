package com.example.energy360mobile.databases.exercises

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Data source class for interacting with Firebase Firestore to manage exercise information in the cloud.
 */
class ExercisesFirebaseDataSource {

    private val db = FirebaseFirestore.getInstance()
    private val collectionName = "exercises"

    /**
     * Fetches all available exercises from the cloud repository.
     * This is a network operation and is performed asynchronously.
     *
     * @return A list of [ExercisesEntity] objects, or an empty list if a network error occurs.
     */
    suspend fun fetchAllExercises(): List<ExercisesEntity> {
        return try {
            val snapshot = db.collection(collectionName).get().await()
            Log.d("ExercisesFirebase", snapshot.toString())
            snapshot.toObjects(ExercisesEntity::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Saves or updates an exercise record in the cloud database.
     * It uses the exercise's ID as the document ID in Firestore.
     *
     * @param exercise The [ExercisesEntity] to be saved or updated.
     */
    suspend fun saveExercise(exercise: ExercisesEntity) {
        try {
            db.collection(collectionName)
                .document(exercise.id.toString())
                .set(exercise)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}