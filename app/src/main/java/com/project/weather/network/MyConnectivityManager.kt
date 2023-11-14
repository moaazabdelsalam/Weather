package com.project.weather.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MyConnectivityManager(private val context: Context) {
    private var _isConnected: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean>
        get() = _isConnected
    private val networkRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onUnavailable() {
            super.onUnavailable()
            Log.i("TAG", "onUnavailable: ")
            _isConnected.value = false
        }

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.i("TAG", "onAvailable: ")
            _isConnected.value = true
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Log.i("TAG", "onLost: ")
            _isConnected.value = false
        }
    }

    private val connectivityManager = getSystemService(
        context,
        ConnectivityManager::class.java
    ) as ConnectivityManager

    init {
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

    fun isConnectedToWifi(): Boolean {
        connectivityManager.requestNetwork(networkRequest, networkCallback)
        return (connectivityManager.activeNetwork != null)
    }
}