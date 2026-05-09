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

class ServicesViewModel (application: Application) : AndroidViewModel(application){

    private val repository: ServicesRepository

    private val servicesFlow = MutableStateFlow<List<ServicesEntity>>(emptyList())

    val services: StateFlow<List<ServicesEntity>>
        get() = servicesFlow

    init {
        val database = ServicesDatabase.getDatabase(application)
        val dao = database.servicesDao()
        repository = ServicesRepository(dao)

        viewModelScope.launch {
            repository.allServices
                .collect { servicesFlow.value = it }
       }
    }

}