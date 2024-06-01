package com.kaimdev.zclip_android.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.kaimdev.zclip_android.event_args.ListenerEventArgs
import com.kaimdev.zclip_android.event_args.ListenerEventType
import com.kaimdev.zclip_android.interfaces.IObserver
import com.kaimdev.zclip_android.helpers.ServiceExtensions.Companion.subscribe
import com.kaimdev.zclip_android.interfaces.IClipboardService
import com.kaimdev.zclip_android.interfaces.IEventArgs
import com.kaimdev.zclip_android.interfaces.IListenerService
import com.kaimdev.zclip_android.interfaces.IService
import com.kaimdev.zclip_android.interfaces.ISyncService
import com.kaimdev.zclip_android.stores.DataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SyncService : Service(), ISyncService, IObserver
{
    @Inject
    lateinit var clipboardService: ClipboardService

    @Inject
    lateinit var notificationService: NotificationService

    @Inject
    lateinit var listenerService: ListenerService

    @Inject
    lateinit var dataStore: DataStore

    private val binder = LocalBinder()

    private val isSyncFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private var isSyncStaticState: Boolean = false

    override fun isSync(): Flow<Boolean>
    {
        return isSyncFlow
    }

    companion object
    {
        const val ACTION_SEND_CLIPBOARD_CONTENT =
            "com.kaimdev.zclip_android.ACTION_SEND_CLIPBOARD_CONTENT"
    }

    inner class LocalBinder : Binder()
    {
        fun getService(): SyncService
        {
            return this@SyncService
        }
    }

    override fun onCreate()
    {
        super.onCreate()

        clipboardService.subscribe(this)
        notificationService.subscribe(this)
        listenerService.subscribe(this)
    }

    override fun onBind(intent: Intent?): IBinder
    {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean
    {
        clipboardService.stop()
        notificationService.stop()
        listenerService.stop()

        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        when (intent?.action)
        {
            ACTION_SEND_CLIPBOARD_CONTENT -> sendClipboardContent()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun start()
    {
        listenerService.start()
        observeSyncState()
    }

    override fun stop()
    {
        clipboardService.stop()
        notificationService.stop()
        listenerService.stop()

        isSyncFlow.value = false
    }

    override fun sendClipboardContent()
    {
        clipboardService.getManualClipboard()
    }

    override fun <TService : IService> notify(sender: TService, eventArgs: IEventArgs)
    {
        when (sender)
        {
            is IClipboardService ->
            {
            }

            is IListenerService  ->
            {
                handleListenerService(eventArgs as ListenerEventArgs)
            }
        }
    }

    private fun observeSyncState()
    {
        CoroutineScope(Dispatchers.IO).launch {
            isSync().collect {

                isSyncStaticState = it

                if (it)
                {
                    clipboardService.start()
                    notificationService.start()
                }
                else
                {
                    stop()
                }
            }
        }
    }

    private fun handleListenerService(listenerEventArgs: ListenerEventArgs)
    {
        when (listenerEventArgs.listenerEvenType)
        {
            ListenerEventType.RequestConnection ->
            {
                CoroutineScope(Dispatchers.IO).launch {
                    var filter = true
                    var savedIp: String? = null
                    val ipFromRequest = listenerEventArgs.ip

                    dataStore.getTargetIp().filter { filter }.collect {
                        savedIp = it
                        filter = false
                    }

                    if (savedIp == ipFromRequest)
                    {
                        isSyncFlow.value = true
                    } else
                    {
                        notificationService.showRequestConnectionNotification()
                    }
                }
            }

            ListenerEventType.ReplyRequest      ->
            {
                if (isSyncStaticState)
                {
                    listenerEventArgs.callBack?.invoke(false)
                    return
                }

                // TODO: Implement the SecurityService to validate and save the code

                isSyncFlow.value = true
                dataStore.setTargetIp(listenerEventArgs.ip!!)
            }

            ListenerEventType.Disconnect        ->
            {
                isSyncFlow.value = false
                dataStore.deleteTargetIp()
            }

            ListenerEventType.ClipboardContent  ->
            {
                clipboardService.setClipboard(listenerEventArgs.message!!)
            }
        }
    }
}