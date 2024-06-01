package com.kaimdev.zclip_android.fragments

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModel
import com.kaimdev.zclip_android.event_args.SyncServiceEventArgs
import com.kaimdev.zclip_android.helpers.ServiceExtensions.Companion.subscribe
import com.kaimdev.zclip_android.interfaces.IEventArgs
import com.kaimdev.zclip_android.interfaces.IObserver
import com.kaimdev.zclip_android.interfaces.IService
import com.kaimdev.zclip_android.interfaces.ISyncService
import com.kaimdev.zclip_android.services.SyncService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) :
    ViewModel(), IObserver
{
    private val isSyncFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val showDialogFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private var syncService: ISyncService? = null

    private val syncServiceConnection = object : ServiceConnection
    {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?)
        {
            val binder = service as SyncService.LocalBinder
            syncService = binder.getService()
            syncService!!.start()

            syncService!!.subscribe(this@HomeViewModel)

            CoroutineScope(Dispatchers.IO).launch {
                syncService!!.isSync().collect {
                    isSyncFlow.value = it
                }
            }
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

    fun sendClipboard()
    {
        syncService?.sendClipboardContent()
    }

    fun acceptConnection()
    {
        syncService?.allowConnection()
    }

    fun denyConnection()
    {
        syncService?.denyConnection()
    }

    override fun <TService : IService> notify(sender: TService, eventArgs: IEventArgs)
    {
        CoroutineScope(Dispatchers.Main).launch {
            when (sender)
            {
                is ISyncService ->
                {
                    showDialogFlow.value = (eventArgs as SyncServiceEventArgs).showRequestConnectionDialog
                }
            }
        }
    }
}