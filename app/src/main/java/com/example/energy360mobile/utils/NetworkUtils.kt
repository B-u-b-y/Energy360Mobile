package com.example.energy360mobile.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * Interface defining a mechanism to observe changes in the network connectivity status.
 */
interface NetworkObserver {
    /**
     * Starts observing network changes and returns a [Flow] of [Status].
     */
    fun observe(): Flow<Status>

    /**
     * Represents the possible states of network connectivity.
     */
    enum class Status {
        Available, Unavailable, Losing, Lost
    }
}

/**
 * Implementation of [NetworkObserver] using the Android [ConnectivityManager].
 * It provides a reactive way to monitor network state changes.
 *
 * @param context The application context required to access system services.
 */
class ConnectivityObserver(context: Context): NetworkObserver {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * Observes the default network's connectivity status.
     * Uses [callbackFlow] to wrap the [ConnectivityManager.NetworkCallback] and emit status updates.
     *
     * @return A [Flow] emitting the current [NetworkObserver.Status].
     */
    override fun observe(): Flow<NetworkObserver.Status> {
        return callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    launch { send(NetworkObserver.Status.Available) }
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    launch { send(NetworkObserver.Status.Losing) }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    launch { send(NetworkObserver.Status.Lost) }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    launch { send(NetworkObserver.Status.Unavailable) }
                }
            }

            connectivityManager.registerDefaultNetworkCallback(callback)
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged()
    }
}

/**
 * Utility function for an instantaneous check of the network connection.
 * It checks if the device is currently connected to Wi-Fi, Cellular, or Ethernet.
 *
 * @param context The context used to retrieve the ConnectivityManager.
 * @return True if a network transport is available, false otherwise.
 */
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}
