package com.kaimdev.zclip_android.fragments

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModel
import com.kaimdev.zclip_android.interfaces.ISyncService
import com.kaimdev.zclip_android.services.SyncService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) :
    ViewModel()
{
    private val isSyncFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private var syncService: ISyncService? = null

    private val syncServiceConnection = object : ServiceConnection
    {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?)
        {
            val binder = service as SyncService.LocalBinder
            syncService = binder.getService()
            syncService!!.start()
            isSyncFlow.value = syncService!!.isSync()
        }

        override fun onServiceDisconnected(name: ComponentName?)
        {
            syncService = null
        }
    }

    fun startSyncService()
    {
        if (syncService == null)
        {
            val intent = Intent(context, SyncService::class.java)
            context.bindService(intent, syncServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    fun stopSyncService()
    {
        syncService?.let {
            context.unbindService(syncServiceConnection)
        }

        syncService = null

        isSyncFlow.value = false
    }

    fun isSync(): Flow<Boolean>
    {
        return isSyncFlow
    }
}