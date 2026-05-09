package com.example.energy360mobile.databases.services;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ServicesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(service : ServicesEntity)

    @Query("SELECT * FROM services")
    fun getAllServices(): Flow<List<ServicesEntity>>

    @Query("SELECT * FROM services WHERE id = :id")
    suspend fun getServiceById(id: Int): ServicesEntity?

}
