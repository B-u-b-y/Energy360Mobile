package com.example.energy360mobile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.energy360mobile.databases.userData.UserDatabase
import com.example.energy360mobile.databases.userData.UserEntity
import com.example.energy360mobile.databases.userData.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel that handles the user logic between DB and UI
 */
class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository

    // Handle subscription and deletion
    private val userDataFlow: StateFlow<UserEntity?>

    val userData: StateFlow<UserEntity?>
        get() = userDataFlow

    init {
        val database = UserDatabase.getDatabase(application)
        val dao = database.userDao()
        repository = UserRepository(dao)

        // Initialized userData
        userDataFlow = repository.user.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null // Starts with null until the database is reached
        )
    }

    fun insertUserData(userData: UserEntity) {
        viewModelScope.launch {
            repository.insertUser(userData)
        }
    }
}