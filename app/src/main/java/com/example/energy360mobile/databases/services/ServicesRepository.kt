package com.example.energy360mobile.databases.services

import kotlinx.coroutines.flow.Flow

class ServicesRepository(private val dao: ServicesDao) {

    val allServices: Flow<List<ServicesEntity>> = dao.getAllServices()

    suspend fun insertService(service: ServicesEntity) {
        dao.insert(service)
    }

    suspend fun getServiceById(id: Int): ServicesEntity? {
        return dao.getServiceById(id)
    }
}
