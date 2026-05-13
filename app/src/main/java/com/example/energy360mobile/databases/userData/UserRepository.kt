package com.example.energy360mobile.databases.userData

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
        val userFirebaseRef = firebaseDataSource.saveUser(user) // Backup in background su Firebase
        user.id = userFirebaseRef?.id.toString()
        dao.insert(user)
    }

    suspend fun removeUser(){
        dao.clearAll()
    }

    fun checkIfEmailExists(userData: UserEntity): Boolean{
        try{
            return firebaseDataSource.insertUserData(userData)
        }catch (e: Exception){

        }
        return false
    }

    fun fetchUserData(email: String): UserEntity? {
        try {
            return firebaseDataSource.fetchUserData(email)
        } catch (e: Exception) {
            // L'app continuerà a usare la cache di Room senza bloccare l'utente
        }
        return null
    }
}
