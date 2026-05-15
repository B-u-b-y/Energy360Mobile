package com.example.energy360mobile

import com.example.energy360mobile.databases.exercises.ExercisesDao
import com.example.energy360mobile.databases.exercises.ExercisesEntity
import com.example.energy360mobile.databases.exercises.ExercisesFirebaseDataSource
import com.example.energy360mobile.databases.exercises.ExercisesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ExercisesRepositoryTest {

    private val dao = mockk<ExercisesDao>(relaxed = true)
    private val firebase = mockk<ExercisesFirebaseDataSource>(relaxed = true)

    private val repository = ExercisesRepository(dao, firebase)

    @Test
    fun insertExercise_callsDaoInsert() = runBlocking {
        val exercise = ExercisesEntity(name = "Bench")

        repository.insertExercise(exercise)

        coVerify { dao.insert(exercise) }
    }

    @Test
    fun deleteAll_callsDaoDeleteAll() = runBlocking {
        repository.deleteAll()

        coVerify { dao.deleteAll() }
    }

    @Test
    fun syncExercisesFromCloud_insertsRemoteExercises() = runBlocking {
        val remoteExercises = listOf(
            ExercisesEntity(name = "Bench"),
            ExercisesEntity(name = "Squat")
        )

        coEvery { firebase.fetchAllExercises() } returns remoteExercises

        repository.syncExercisesFromCloud()

        coVerify(exactly = 2) { dao.insert(any()) }
    }
}