package com.example.energy360mobile.databases.userData

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.android.gms.tasks.Tasks
import java.util.concurrent.TimeUnit

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

    fun insertUserData(user: UserEntity): Boolean {

        return try {
            val task = db.collection(collectionName)
                .whereEqualTo("email", user.email)
                .get()

            val snapshot = Tasks.await(task, 5, TimeUnit.SECONDS)
            if (!snapshot.isEmpty) {
                val user = snapshot.documents.first().data
                user != null
            } else {
                false
            }
        }catch (e: Exception){false}
    }

    /**
     * Salva o aggiorna i dati dell'utente sul Cloud.
     */
    suspend fun saveUser(user: UserEntity): DocumentReference? {
        return try {
            val usersCollection = db.collection(collectionName)

            val existingUserQuery = usersCollection
                .whereEqualTo("email", user.email)
                .get()
                .await()

            if (!existingUserQuery.isEmpty) {
                return existingUserQuery.documents.first().reference
            }

            val documentRef = usersCollection
                .add(user)
                .await()

            Log.d("saveUser", "Utente '${user.email}' salvato con ID: ${documentRef.id}")
            documentRef

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("saveUser", "Errore durante il salvataggio dell'utente: ${e.message}")
            null
        }
    }

    /**
     * Scarica i dati dell'utente dal Cloud usando la mail.
     * Ritorna UserEntity se lo trova, altrimenti null (es. utente nuovo o offline).
     */
    fun fetchUserData(email: String): UserEntity? {
        return try {
            val task = db.collection(collectionName)
                .whereEqualTo("email", email)
                .get()

            val snapshot = Tasks.await(task, 5, TimeUnit.SECONDS)
            if (!snapshot.isEmpty) {

                val user = snapshot.documents.first().data
                val users = UserEntity(
                    "",
                    name = user?.get("name").toString(),
                    password = user?.get("password").toString(),
                    email = user?.get("email").toString(),
                    dob = user?.get("dob") as Long
                )
                users
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}