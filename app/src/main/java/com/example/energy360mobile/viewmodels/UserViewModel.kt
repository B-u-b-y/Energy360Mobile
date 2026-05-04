package com.example.energy360mobile.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.energy360mobile.databases.userData.UserEntity
import com.example.energy360mobile.databases.userData.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel che gestisce la logica dei dati utente tra il database e la UI.
 */
class UserViewModel(private val repository: UserRepository) : ViewModel() {

    /**
     * StateFlow che espone i dati dell'utente alla UI in modo reattivo.
     * Utilizza 'stateIn' per convertire il Flow del repository in uno StateFlow
     * legato al ciclo di vita del ViewModel.
     */
    val userData: StateFlow<UserEntity?> = repository.user
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Mantiene il flow attivo per 5 secondi dopo che la UI sparisce
            initialValue = null
        )

    /**
     * Inserisce o aggiorna un utente nel database in modo asincrono.
     */
    fun insertUser(user: UserEntity) {
        viewModelScope.launch {
            repository.insertUser(user)
        }
    }
}

/**
 * Factory necessaria per istanziare il ViewModel passando il Repository come parametro.
 */
class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe ViewModel sconosciuta")
    }
}
