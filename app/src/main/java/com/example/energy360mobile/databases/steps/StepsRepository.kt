package com.example.energy360mobile.databases.steps

import kotlinx.coroutines.flow.Flow

/**
 * Repository class that abstracts access to step counting data from the local database.
 *
 * @property dao The [StepsDao] for performing database operations.
 */
class StepsRepository(private val dao: StepsDao) {

    /**
     * A [Flow] emitting a list of all recorded step entries, ordered by time.
     */
    val allSteps: Flow<List<StepsEntity>> = dao.getAllSteps()

    /**
     * A [Flow] emitting the most recent step entry.
     */
    val latestSteps: Flow<StepsEntity?> = dao.getLatestSteps()

    /**
     * Inserts a new step record into the local database.
     *
     * @param steps The [StepsEntity] to be inserted.
     */
    suspend fun insertSteps(steps: StepsEntity) {
        dao.insertSteps(steps)
    }
}