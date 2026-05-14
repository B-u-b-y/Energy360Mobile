package com.example.energy360mobile.databases.services

import kotlinx.coroutines.flow.Flow

/**
 * Repository class that abstracts access to the fitness services data source.
 *
 * @property dao The [ServicesDao] used for database operations.
 */
class ServicesRepository(private val dao: ServicesDao) {

    /**
     * A [Flow] emitting a list of all services available in the local database.
     */
    val allServices: Flow<List<ServicesEntity>> = dao.getAllServices()

    /**
     * Inserts a new service into the local database.
     *
     * @param service The [ServicesEntity] to be inserted.
     */
    suspend fun insertService(service: ServicesEntity) {
        dao.insert(service)
    }

    /**
     * Retrieves a service by its unique ID.
     *
     * @param id The unique ID of the service.
     * @return The [ServicesEntity] if found, otherwise null.
     */
    suspend fun getServiceById(id: Int): ServicesEntity? {
        return dao.getServiceById(id)
    }
}
