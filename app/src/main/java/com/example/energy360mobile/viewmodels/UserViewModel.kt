package com.example.energy360mobile.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.energy360mobile.databases.userData.UserDatabase
import com.example.energy360mobile.databases.userData.UserEntity
import com.example.energy360mobile.databases.userData.UserRepository
import com.example.energy360mobile.databases.userData.UserFirebaseDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel that handles the user logic between DB and UI
 */
class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository

    // Handle subscription and deletion
    private var userDataFlow: StateFlow<UserEntity?>

    val userData: StateFlow<UserEntity?>
        get() = userDataFlow

    init {
        val database = UserDatabase.getDatabase(application)
        val dao = database.userDao()
        val firebaseDataSource = UserFirebaseDataSource()
        repository = UserRepository(dao,firebaseDataSource)

        // Initialized userData
        userDataFlow = repository.user.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null // Starts with null until the database is reached
        )
    }

    /**
     * Avvia il download dei dati da Firebase per aggiornare Room.
     * Da chiamare nella UI, ad esempio non appena l'utente effettua il login.
     */
    fun syncUserFromCloud(userId: String) {
        viewModelScope.launch {
            repository.syncUserFromCloud(userId)
        }
    }

    fun removeUserData(){
        viewModelScope.launch {
            repository.removeUser()
        }
    }

    suspend fun registerUser(userData: UserEntity) : Boolean{
        return withContext(Dispatchers.IO) {
            repository.checkIfEmailExists(userData)
        }
    }

    suspend fun checkCredentials(email: String, password: String): Boolean{
        return withContext(Dispatchers.IO) {
            val fetchedUser = repository.fetchUserData(email)
            if (fetchedUser != null && fetchedUser.email == email && fetchedUser.password == password) {
                repository.insertUser(fetchedUser)
                true
            } else {
                false
            }
        }
    }
}