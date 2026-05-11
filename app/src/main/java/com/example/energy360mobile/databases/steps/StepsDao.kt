package com.example.energy360mobile.databases.steps

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy


import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface StepsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(step: StepsEntity)

    @Query("SELECT * FROM steps ORDER BY timeInMsecs DESC LIMIT 1")
    fun getLatestSteps(): Flow<StepsEntity?>

    @Query("SELECT * FROM steps ORDER BY timeInMsecs DESC")
    fun getAllSteps(): Flow<List<StepsEntity>>
}