package com.example.energy360mobile.databases.userData

import com.example.energy360mobile.databases.userData.UserFirebaseDataSource
import com.google.firebase.firestore.auth.User

class UserRepository (private val dao: UserDao,private val firebaseDataSource: UserFirebaseDataSource){

    val user = dao.getUser()

    /**
     * Sincronizza l'utente da Firebase a Room.
     * Chiamerai questa funzione nel ViewModel al login o all'avvio dell'app.
     */
    suspend fun syncUserFromCloud(userId: String) {
        try {
            val remoteUser = firebaseDataSource.fetchUser(userId)
            if (remoteUser != null) {
                dao.insert(remoteUser)
            }
        } catch (e: Exception) {
            // L'app continuerà a usare la cache di Room senza bloccare l'utente
        }
    }

    /**
     * Inserisce/Aggiorna l'utente salvandolo contemporaneamente in locale e sul cloud.
     */
    suspend fun insertUser(user: UserEntity) {
        val userFirebaseId = firebaseDataSource.saveUser(user) // Backup in background su Firebase
        user.id = userFirebaseId?.id.toString()
        dao.insert(user)
    }

    suspend fun removeUser(){
        dao.clearAll()
    }
}
