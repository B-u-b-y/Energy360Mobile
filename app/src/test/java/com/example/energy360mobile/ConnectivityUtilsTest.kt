package com.example.energy360mobile

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.example.energy360mobile.utils.isNetworkAvailable
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ConnectivityUtilsTest {

    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var network: Network
    private lateinit var capabilities: NetworkCapabilities

    @Before
    fun setup() {
        context = mockk()
        connectivityManager = mockk()
        network = mockk()
        capabilities = mockk()

        every {
            context.getSystemService(Context.CONNECTIVITY_SERVICE)
        } returns connectivityManager
    }

    @Test
    fun `returns false when no active network`() {
        every { connectivityManager.activeNetwork } returns null

        val result = isNetworkAvailable(context)

        assertFalse(result)
    }

    @Test
    fun `returns false when network capabilities are null`() {
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns null

        val result = isNetworkAvailable(context)

        assertFalse(result)
    }

    @Test
    fun `returns true when wifi is available`() {
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns true
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns false
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) } returns false

        val result = isNetworkAvailable(context)

        assertTrue(result)
    }

    @Test
    fun `returns true when cellular is available`() {
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns false
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns true
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) } returns false

        val result = isNetworkAvailable(context)

        assertTrue(result)
    }

    @Test
    fun `returns false when no supported transport exists`() {
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasTransport(any()) } returns false

        val result = isNetworkAvailable(context)

        assertFalse(result)
    }
}