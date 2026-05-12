package com.example.energy360mobile.databases.steps

import kotlinx.coroutines.flow.Flow

class StepsRepository(private val dao: StepsDao) {

    val allSteps: Flow<List<StepsEntity>> = dao.getAllSteps()
    val latestSteps: Flow<StepsEntity?> = dao.getLatestSteps()

    suspend fun insertSteps(steps: StepsEntity) {
        dao.insertSteps(steps)
    }
}