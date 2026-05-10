package com.example.energy360mobile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.energy360mobile.databases.reservations.ReservationsDatabase
import com.example.energy360mobile.databases.reservations.ReservationsEntity
import com.example.energy360mobile.databases.reservations.ReservationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReservationsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ReservationsRepository

    private val reservationsFlow = MutableStateFlow<List<ReservationsEntity>>(emptyList())
    val reservations: StateFlow<List<ReservationsEntity>>
        get() = reservationsFlow

    init {
        val database = ReservationsDatabase.getDatabase(application)
        val dao = database.reservationsDao()

        repository = ReservationsRepository(dao)

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

    fun insertReservation(reservation: ReservationsEntity) {
        viewModelScope.launch {
            repository.insertReservations(reservation)
        }
    }
}
