package com.kaimdev.zclip_android.modules

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat
import com.kaimdev.zclip_android.models.LocalIpModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.kaimdev.zclip_android.R

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule
{
    @SuppressLint("DefaultLocale")
    @Provides
    fun provideLocalIpAddress(@ApplicationContext context: Context): LocalIpModel
    {
        var localIpModel: LocalIpModel? = null

        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

        if (wifiManager.isWifiEnabled)
        {
            val connectivityManager = ContextCompat.getSystemService(
                context,
                ConnectivityManager::class.java
            ) as ConnectivityManager

            val linkProperties =
                connectivityManager.getLinkProperties(connectivityManager.activeNetwork)

            linkProperties?.linkAddresses?.forEach {
                val ip = it.address.hostAddress
                if (ip != null)
                {
                    if (ip.startsWith("192.168."))
                    {
                        localIpModel = LocalIpModel(ip, false)
                        return@forEach
                    }
                }
            }
        }

        if (localIpModel != null)
        {
            return localIpModel!!
        }

        val message = context.getString(R.string.network_not_detected)
        return LocalIpModel(message, true)
    }
}
