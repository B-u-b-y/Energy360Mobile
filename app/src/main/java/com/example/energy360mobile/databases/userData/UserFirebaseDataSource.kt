package com.example.energy360mobile.databases.userData

import com.example.energy360mobile.databases.userData.UserEntity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserFirebaseDataSource {

    private val db = FirebaseFirestore.getInstance()
    private val collectionName = "users"

    /**
     * Scarica i dati dell'utente dal Cloud usando il suo ID univoco.
     * Ritorna UserEntity se lo trova, altrimenti null (es. utente nuovo o offline).
     */
    suspend fun fetchUser(userId: String): UserEntity? {
        return try {
            val snapshot = db.collection(collectionName).document(userId).get().await()
            // Converte il documento Firestore nell'oggetto UserEntity
            snapshot.toObject(UserEntity::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Salva o aggiorna i dati dell'utente sul Cloud.
     */
    suspend fun saveUser(user: UserEntity): DocumentReference? {
        try {
            // NOTA: Assicurati che 'UserEntity' abbia un campo id.
            // Sostituisci 'user.id.toString()' con il campo corretto se si chiama diversamente.
            return db.collection(collectionName)
                .add(user)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}