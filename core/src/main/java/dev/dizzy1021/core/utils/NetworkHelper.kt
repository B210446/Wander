package dev.dizzy1021.core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

@SuppressLint("MissingPermission")
fun isNetworkAvailable(ctx: Context): Boolean {
    val connectivityManager =
        ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        connectivityManager
            .getNetworkCapabilities(connectivityManager.activeNetwork)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            ?: false
    } else {
        connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!
            .isConnected
    }
}