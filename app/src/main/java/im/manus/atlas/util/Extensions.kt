package im.manus.atlas.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

fun Double.formatCoordinate(): String = String.format("%.4f", this)

fun <T> List<T>.applyWhen(condition: Boolean, block: List<T>.() -> List<T>): List<T> {
    return if (condition) block() else this
}
