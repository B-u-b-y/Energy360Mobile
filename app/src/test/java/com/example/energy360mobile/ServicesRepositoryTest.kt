package com.example.energy360mobile

import com.example.energy360mobile.databases.services.ServicesDao
import com.example.energy360mobile.databases.services.ServicesEntity
import com.example.energy360mobile.databases.services.ServicesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [ServicesRepository].
 * Verifies that the repository correctly delegates operations to the [ServicesDao].
 */
class ServicesRepositoryTest {

    private val dao = mockk<ServicesDao>(relaxed = true)
    private val repository = ServicesRepository(dao)

    /**
     * Verifies that [ServicesRepository.insertService] calls the DAO's insert method.
     */
    @Test
    fun insertService_callsDaoInsert() = runBlocking {
        val service = ServicesEntity(
            id = 1,
            name = "Yoga",
            description = "Test Description",
            category = "Wellness",
            duration = "60 min",
            date = 123456789L,
            place = "Room A"
        )

        repository.insertService(service)

        coVerify { dao.insert(service) }
    }

    /**
     * Verifies that [ServicesRepository.getServiceById] returns the correct service from the DAO.
     */
    @Test
    fun getServiceById_returnsServiceFromDao() = runBlocking {
        val service = ServicesEntity(
            id = 10,
            name = "Pilates",
            description = "Test",
            category = "Wellness",
            duration = "45 min",
            date = 1000L,
            place = "Room B"
        )
        coEvery { dao.getServiceById(10) } returns service

        val result = repository.getServiceById(10)

        assertEquals(service, result)
        coVerify { dao.getServiceById(10) }
    }

    /**
     * Verifies that [ServicesRepository.getServiceById] returns null when the DAO returns null.
     */
    @Test
    fun getServiceById_returnsNullWhenNotFound() = runBlocking {
        coEvery { dao.getServiceById(any()) } returns null

        val result = repository.getServiceById(99)

        assertEquals(null, result)
    }
}
