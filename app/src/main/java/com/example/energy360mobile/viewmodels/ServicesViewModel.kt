package com.example.energy360mobile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.energy360mobile.databases.services.ServicesDatabase
import com.example.energy360mobile.databases.services.ServicesEntity
import com.example.energy360mobile.databases.services.ServicesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing fitness services and courses.
 * It acts as an intermediary between the [ServicesRepository] and the UI,
 * providing a reactive list of all available services.
 *
 * @param application The application context.
 */
class ServicesViewModel (application: Application) : AndroidViewModel(application){

    private val repository: ServicesRepository

    // Internal mutable state flow to hold the list of services
    private val servicesFlow = MutableStateFlow<List<ServicesEntity>>(emptyList())

    /**
     * A StateFlow emitting the list of all services available in the database.
     */
    val services: StateFlow<List<ServicesEntity>>
        get() = servicesFlow

    init {
        val database = ServicesDatabase.getDatabase(application)
        val dao = database.servicesDao()
        repository = ServicesRepository(dao)

        // Observe the database flow and update the state flow
        viewModelScope.launch {
            repository.allServices
                .collect { servicesFlow.value = it }
       }
    }

}