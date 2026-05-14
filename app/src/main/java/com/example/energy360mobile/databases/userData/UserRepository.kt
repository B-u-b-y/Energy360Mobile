package com.example.energy360mobile.databases.userData

import kotlinx.coroutines.flow.Flow

/**
 * Repository class that abstracts access to user data from both local (Room)
 * and remote (Firebase) data sources.
 *
 * @property dao The [UserDao] for local database operations.
 * @property firebaseDataSource The [UserFirebaseDataSource] for remote database operations.
 */
class UserRepository (private val dao: UserDao, private val firebaseDataSource: UserFirebaseDataSource){

    /**
     * A [Flow] emitting the current user from the local database.
     */
    val user: Flow<UserEntity?> = dao.getUser()

    /**
     * Synchronizes user data from Firebase to the local Room database.
     * This is typically called in the ViewModel upon login or app startup.
     *
     * @param userId The unique ID of the user to sync.
     */
    suspend fun syncUserFromCloud(userId: String) {
        try {
            val remoteUser = firebaseDataSource.fetchUser(userId)
            if (remoteUser != null) {
                dao.insert(remoteUser)
            }
        } catch (e: Exception) {
            // App continues using Room cache without blocking the user
        }
    }

    /**
     * Inserts or updates a user, saving them both to the local database and the cloud.
     *
     * @param user The [UserEntity] to be saved.
     */
    suspend fun insertUser(user: UserEntity) {
        val userFirebaseRef = firebaseDataSource.saveUser(user) // Cloud backup
        user.id = userFirebaseRef?.id.toString()
        dao.insert(user)
    }

    /**
     * Removes the user data from the local database.
     */
    suspend fun removeUser(){
        dao.clearAll()
    }

    /**
     * Checks if a user with the given email already exists in Firebase.
     *
     * @param userData The user data containing the email to check.
     * @return True if the email exists, false otherwise.
     */
    fun checkIfEmailExists(userData: UserEntity): Boolean{
        try{
            return firebaseDataSource.insertUserData(userData)
        }catch (e: Exception){

        }
        return false
    }

    /**
     * Fetches user data from Firebase using their email.
     *
     * @param email The user's email address.
     * @return The [UserEntity] if found, otherwise null.
     */
    fun fetchUserData(email: String): UserEntity? {
        try {
            return firebaseDataSource.fetchUserData(email)
        } catch (e: Exception) {
            // App continues using Room cache
        }
        return null
    }
}
