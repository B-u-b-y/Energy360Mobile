package com.example.energy360mobile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.energy360mobile.databases.reservations.ReservationsDatabase
import com.example.energy360mobile.databases.reservations.ReservationsEntity
import com.example.energy360mobile.databases.reservations.ReservationsRepository
import com.example.energy360mobile.databases.reservations.ReservationsFirebaseDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReservationsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ReservationsRepository

    private val reservationsFlow = MutableStateFlow<List<ReservationsEntity>>(emptyList())
    val reservations: StateFlow<List<ReservationsEntity>>
        get() = reservationsFlow

    init {
        val database = ReservationsDatabase.getDatabase(application)
        val dao = database.reservationsDao()
        val firebaseDataSource = ReservationsFirebaseDataSource()

        repository = ReservationsRepository(dao,firebaseDataSource)

        viewModelScope.launch {
            repository.allReservations
                .collect { reservationsFlow.value = it }
        }
    }

    fun deleteReservation(reservation: ReservationsEntity) {
        viewModelScope.launch {
            repository.deleteReservation(reservation)
        }
    }

    suspend fun insertReservation(reservation: ReservationsEntity) {
        return withContext(Dispatchers.IO){
            repository.insertReservations(reservation)
        }
    }
}
