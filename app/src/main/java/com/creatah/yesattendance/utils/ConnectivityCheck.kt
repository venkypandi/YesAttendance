package com.creatah.yesattendance.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class ConnectivityCheck(private val connectivityCallback: ConnectivityCallback) :
    BroadcastReceiver() {

    interface ConnectivityCallback {
        fun turnedOn()
        fun turnedOff()
    }

    override fun onReceive(context: Context, intent: Intent) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val activeNetwork = connectivityManager.getNetworkCapabilities(network)
        if (activeNetwork == null || network == null) {
            connectivityCallback.turnedOff()
            return
        }
        when{
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> connectivityCallback.turnedOn()
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> connectivityCallback.turnedOn()
            else -> connectivityCallback.turnedOff()
        }
    }
}