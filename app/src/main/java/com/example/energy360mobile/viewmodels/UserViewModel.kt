package com.example.energy360mobile.viewmodels

import android.app.Application
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
 * ViewModel that handles user-related business logic, bridging the UI and the data layer (Repository).
 * It manages user authentication, profile data synchronization, and local storage.
 *
 * @param application The application instance used to initialize the local database.
 */
class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository

    // Internal flow handling the subscription to user data changes
    private var userDataFlow: StateFlow<UserEntity?>

    /**
     * A StateFlow emitting the current user data from the local database.
     * Can be null if no user is logged in.
     */
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
     * Clears the current user data from the local database.
     */
    fun removeUserData(){
        viewModelScope.launch {
            repository.removeUser()
        }
    }

    /**
     * Registers a new user. Checks if the email already exists before proceeding.
     *
     * @param userData The UserEntity containing the information of the user to register.
     * @return True if the email already exists (registration failed), false otherwise.
     */
    suspend fun registerUser(userData: UserEntity) : Boolean{
        return withContext(Dispatchers.IO) {
            repository.checkIfEmailExists(userData)
        }
    }

    /**
     * Validates user credentials against the remote data source.
     * If valid, the user data is cached in the local database.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return True if the credentials are valid and login is successful, false otherwise.
     */
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