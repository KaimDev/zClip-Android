package com.kaimdev.zclip_android.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.widget.Toast
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
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.kaimdev.zclip_android.event_args.SyncServiceEventArgs
import com.kaimdev.zclip_android.helpers.ServiceExtensions.Companion.sendNotification

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

        const val ACTION_ACCEPT_REQUEST_CONNECTION =
            "com.kaimdev.zclip_android.ACTION_ACCEPT_REQUEST_CONNECTION"

        const val ACTION_DENY_REQUEST_CONNECTION =
            "com.kaimdev.zclip_android.ACTION_DENY_REQUEST_CONNECTION"
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        when (intent?.action)
        {
            ACTION_SEND_CLIPBOARD_CONTENT    -> sendClipboardContent()
            ACTION_ACCEPT_REQUEST_CONNECTION -> allowConnection()
            ACTION_DENY_REQUEST_CONNECTION   -> denyConnection()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun start()
    {
        listenerService.start()
        notificationService.stop()
    }

    override fun stop()
    {
        clipboardService.stop()
        notificationService.stop()
        listenerService.stop()

        isSyncFlow.value = false
    }

    override fun onBind(intent: Intent?): IBinder
    {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean
    {
        clipboardService.stop()
        listenerService.stop()

        return super.onUnbind(intent)
    }

    override fun sendClipboardContent()
    {
        clipboardService.getManualClipboard()
    }

    override fun allowConnection()
    {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(this@SyncService, "Connection allowed", Toast.LENGTH_SHORT).show()
        }

        notificationService.hideRequestConnectionNotification()
        sendNotification(SyncServiceEventArgs(false))
    }

    override fun denyConnection()
    {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(this@SyncService, "Connection denied", Toast.LENGTH_SHORT).show()
        }

        notificationService.hideRequestConnectionNotification()
        sendNotification(SyncServiceEventArgs(false))
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
                } else
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

                        withContext(Dispatchers.Main)
                        {
                            if (savedIp == ipFromRequest)
                            {
                                isSyncFlow.value = true
                                observeSyncState()
                            } else
                            {
                                notificationService.showRequestConnectionNotification()

                                sendNotification(SyncServiceEventArgs(true))
                            }
                        }
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