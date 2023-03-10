package com.intelab.joblab.domain.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Class that helps us to identify the current state of the network using capabilities.
 * More info: [[https://developer.android.com/reference/android/net/NetworkCapabilities]]
 * */
class Connectivity(context: Context) {

    fun isNetworkAvailable() : Boolean = isNetworkConnected

    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val isNetworkConnected: Boolean
        get() = connectivityManager
            .getNetworkCapabilities(connectivityManager.activeNetwork)
            .isNetworkCapabilitiesValid()

    private fun NetworkCapabilities?.isNetworkCapabilitiesValid(): Boolean = when {
        this == null -> false
        hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                (hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_VPN) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) -> true
        else -> false
    }
}
