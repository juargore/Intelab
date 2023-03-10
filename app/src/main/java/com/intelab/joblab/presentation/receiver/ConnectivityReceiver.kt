package com.intelab.joblab.presentation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.intelab.joblab.domain.common.Connectivity

class ConnectivityReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (connectivityReceiverListener != null) {
            context?.let { c ->
                connectivityReceiverListener!!.onNetworkConnectionChanged(Connectivity(c).isNetworkAvailable())
            }
        }
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
    }
}