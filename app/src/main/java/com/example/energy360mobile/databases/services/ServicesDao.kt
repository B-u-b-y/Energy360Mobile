package com.example.energy360mobile.databases.services

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the services table.
 * Provides methods for interacting with the local services database using Room.
 */
@Dao
interface ServicesDao {

    /**
     * Inserts a service into the database. If the service already exists, it will be replaced.
     *
     * @param service The [ServicesEntity] to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(service: ServicesEntity)

    /**
     * Retrieves all services stored in the database.
     * Returns a [Flow] to observe changes to the services list reactively.
     *
     * @return A [Flow] emitting a list of [ServicesEntity] objects.
     */
    @Query("SELECT * FROM services")
    fun getAllServices(): Flow<List<ServicesEntity>>

    /**
     * Retrieves a specific service by its unique ID.
     *
     * @param id The unique ID of the service.
     * @return The [ServicesEntity] if found, otherwise null.
     */
    @Query("SELECT * FROM services WHERE id = :id")
    suspend fun getServiceById(id: Int): ServicesEntity?
}