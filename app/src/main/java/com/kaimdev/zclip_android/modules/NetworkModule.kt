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
import com.kaimdev.zclip_android.models.ListenerSettingsModel
import com.kaimdev.zclip_android.server.IApplicationApi
import com.kaimdev.zclip_android.stores.DataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule
{
    @Inject
    private lateinit var dataStore: DataStore

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

    @Provides
    fun provideListenerSettingsModel(): ListenerSettingsModel
    {
        return ListenerSettingsModel(1705)
    }

    @Provides
    fun provideRetrofit(): IApplicationApi
    {
        return runBlocking {
            val targetIp = async(Dispatchers.IO) {
                var filter = true
                val targetIpFlow = dataStore.getTargetIp()
                var targetIp: String? = null

                targetIpFlow.filter { filter }.collect {
                    filter = false
                    targetIp = it
                }

                targetIp ?: throw Exception("Target IP not found")
            }.await()

            Retrofit.Builder()
                .baseUrl("http://$targetIp/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(IApplicationApi::class.java)
        }
    }
}
