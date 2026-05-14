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

/**
 * ViewModel responsible for managing user reservations for fitness activities.
 * It coordinates data between the local Room database and the remote Firebase data source
 * through the [ReservationsRepository].
 *
 * @param application The application instance.
 */
class ReservationsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ReservationsRepository

    // Internal mutable flow to manage the list of reservations
    private val reservationsFlow = MutableStateFlow<List<ReservationsEntity>>(emptyList())

    /**
     * A StateFlow emitting the list of all reservations for the current user.
     * Observed by the UI to display booking history and upcoming events.
     */
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

    /**
     * Deletes a specific reservation from both the local database and the remote source.
     *
     * @param reservation The [ReservationsEntity] to be removed.
     */
    fun deleteReservation(reservation: ReservationsEntity) {
        viewModelScope.launch {
            repository.deleteReservation(reservation)
        }
    }

    /**
     * Inserts a new reservation into the system.
     * This operation is performed on the IO dispatcher to avoid blocking the main thread.
     *
     * @param reservation The [ReservationsEntity] representing the new booking.
     */
    suspend fun insertReservation(reservation: ReservationsEntity) {
        return withContext(Dispatchers.IO){
            repository.insertReservations(reservation)
        }
    }
}
