package com.example.energy360mobile.databases.steps

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the steps table.
 * Provides methods for interacting with the local step counting database using Room.
 */
@Dao
interface StepsDao {

    /**
     * Inserts a new step entry into the database. If an entry with the same primary key exists,
     * it will be replaced.
     *
     * @param step The [StepsEntity] to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(step: StepsEntity)

    /**
     * Retrieves the most recent step count entry from the database.
     * Returns a [Flow] to observe changes to the latest steps reactively.
     *
     * @return A [Flow] emitting the latest [StepsEntity], or null if the table is empty.
     */
    @Query("SELECT * FROM steps ORDER BY timeInMsecs DESC LIMIT 1")
    fun getLatestSteps(): Flow<StepsEntity?>

    /**
     * Retrieves all step count entries stored in the database, ordered by time in descending order.
     *
     * @return A [Flow] emitting a list of [StepsEntity] objects.
     */
    @Query("SELECT * FROM steps ORDER BY timeInMsecs DESC")
    fun getAllSteps(): Flow<List<StepsEntity>>
}