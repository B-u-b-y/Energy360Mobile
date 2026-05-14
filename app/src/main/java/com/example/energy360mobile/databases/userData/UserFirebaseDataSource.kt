package com.example.energy360mobile.databases.userData

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.android.gms.tasks.Tasks
import java.util.concurrent.TimeUnit

/**
 * Data source class responsible for interacting with Google Firebase Firestore
 * to manage user-related data in the cloud.
 */
class UserFirebaseDataSource {

    private val db = FirebaseFirestore.getInstance()
    private val collectionName = "users"

    /**
     * Fetches user data from Firestore using their unique ID.
     *
     * @param userId The unique identifier of the user.
     * @return A [UserEntity] if found, otherwise null (e.g., user doesn't exist or device is offline).
     */
    suspend fun fetchUser(userId: String): UserEntity? {
        return try {
            val snapshot = db.collection(collectionName).document(userId).get().await()
            // Converts the Firestore document to a UserEntity object
            snapshot.toObject(UserEntity::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Checks if a user with the same email already exists in Firestore.
     *
     * @param user The user data containing the email to verify.
     * @return True if a user with that email exists, false otherwise.
     */
    fun insertUserData(user: UserEntity): Boolean {
        return try {
            val task = db.collection(collectionName)
                .whereEqualTo("email", user.email)
                .get()

            val snapshot = Tasks.await(task, 5, TimeUnit.SECONDS)
            if (!snapshot.isEmpty) {
                val foundUser = snapshot.documents.first().data
                foundUser != null
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Saves or updates the user data in Firestore.
     * It checks if the email already exists before adding a new document.
     *
     * @param user The [UserEntity] to be saved.
     * @return A [DocumentReference] to the saved user document, or null if an error occurred.
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

            documentRef

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Fetches user data from Firestore using their email address.
     *
     * @param email The user's email address.
     * @return A [UserEntity] if found, otherwise null.
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
                    id = "",
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