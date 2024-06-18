package com.kaimdev.zclip_android.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Process
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
import com.kaimdev.zclip_android.event_args.SyncServiceEventArgs
import com.kaimdev.zclip_android.helpers.ServiceExtensions.Companion.sendNotification
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@AndroidEntryPoint
class SyncService : Service(), ISyncService, IObserver
{
    private val clipboardService: ClipboardService
        get() = serviceDependencies.clipboardService

    private val notificationService: NotificationService
        get() = serviceDependencies.notificationService

    private val listenerService: ListenerService
        get() = serviceDependencies.listenerService

    private val clientService: ClientService
        get() = serviceDependencies.clientService

    private val dataStore: DataStore
        get() = serviceDependencies.dataStore

    private val serviceDependencies: ServiceDependencies
        get() = EntryPoints.get(applicationContext, ServiceDependencies::class.java)

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ServiceDependencies
    {
        val clipboardService: ClipboardService
        val notificationService: NotificationService
        val listenerService: ListenerService
        val clientService: ClientService
        val dataStore: DataStore
    }

    private var serviceLooper: Looper? = null
    private var serviceHandler: SyncServiceHandler? = null

    private inner class SyncServiceHandler(looper: Looper) : Handler(looper)
    {
        override fun handleMessage(msg: Message)
        {
            try
            {
                when (msg.what)
                {
                    ACTION_START_SYNC_SERVICE        ->
                    {
                        CoroutineScope(Dispatchers.IO).launch {

                            listenerService.start()
                            notificationService.start()
                            clientService.requestConnection()
                        }

                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(
                                this@SyncService,
                                "Services subscribed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        clipboardService.subscribe(this@SyncService)
                        notificationService.subscribe(this@SyncService)
                        listenerService.subscribe(this@SyncService)
                        clientService.subscribe(this@SyncService)
                    }

                    ACTION_STOP_SYNC_SERVICE         ->
                    {
                        clipboardService.stop()
                        notificationService.stop()
                        listenerService.stop()

                        isSyncFlow.value = false
                    }

                    ACTION_SEND_CLIPBOARD_CONTENT    ->
                    {
                        clipboardService.getManualClipboard()
                    }

                    ACTION_ACCEPT_REQUEST_CONNECTION ->
                    {
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(
                                this@SyncService,
                                "Connection allowed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        notificationService.hideRequestConnectionNotification()
                        sendNotification(SyncServiceEventArgs(false))
                    }

                    ACTION_DENY_REQUEST_CONNECTION   ->
                    {
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(
                                this@SyncService,
                                "Connection denied",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        notificationService.hideRequestConnectionNotification()
                        sendNotification(SyncServiceEventArgs(false))
                    }

                    else                             ->
                    {

                    }
                }
            } catch (e: InterruptedException)
            {
                Thread.currentThread().interrupt()
            }

            stopSelf(msg.arg1)
        }
    }

    private val binder = LocalBinder()

    private val isSyncFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private var isSyncStaticState: Boolean = false

    override fun isSync(): Flow<Boolean>
    {
        return isSyncFlow
    }

    companion object
    {
        const val ACTION_START_SYNC_SERVICE = 1

        const val ACTION_STOP_SYNC_SERVICE = 2

        const val ACTION_SEND_CLIPBOARD_CONTENT = 3

        const val ACTION_ACCEPT_REQUEST_CONNECTION = 4

        const val ACTION_DENY_REQUEST_CONNECTION = 5
    }

    override fun onCreate()
    {
        super.onCreate()

        Toast.makeText(this, "Sync service created", Toast.LENGTH_SHORT).show()

        HandlerThread("SyncService", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()

            serviceLooper = looper
            serviceHandler = SyncServiceHandler(looper)
        }
    }

    inner class LocalBinder : Binder()
    {
        fun getService(): SyncService
        {
            return this@SyncService
        }
    }

    override fun onBind(intent: Intent?): IBinder
    {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean
    {
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        when (intent?.action)
        {
            ACTION_SEND_CLIPBOARD_CONTENT.toString()    -> sendClipboardContent()
            ACTION_ACCEPT_REQUEST_CONNECTION.toString() -> allowConnection()
            ACTION_DENY_REQUEST_CONNECTION.toString()   -> denyConnection()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun start()
    {
        val msg = serviceHandler?.obtainMessage(ACTION_START_SYNC_SERVICE)
        serviceHandler?.sendMessage(msg!!)
    }

    override fun stop()
    {
        val msg = serviceHandler?.obtainMessage(ACTION_STOP_SYNC_SERVICE)
        serviceHandler?.sendMessage(msg!!)
    }

    override fun sendClipboardContent()
    {
        val msg = serviceHandler?.obtainMessage(ACTION_SEND_CLIPBOARD_CONTENT)
        serviceHandler?.sendMessage(msg!!)
    }

    override fun allowConnection()
    {
        val msg = serviceHandler?.obtainMessage(ACTION_ACCEPT_REQUEST_CONNECTION)
        serviceHandler?.sendMessage(msg!!)
    }

    override fun denyConnection()
    {
        val msg = serviceHandler?.obtainMessage(ACTION_DENY_REQUEST_CONNECTION)
        serviceHandler?.sendMessage(msg!!)
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
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        this@SyncService,
                        "Listener service notified",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SyncService, "Request connection", Toast.LENGTH_SHORT).show()
                    }

                    var filter = true
                    val ipFromRequest = listenerEventArgs.ip

                    dataStore.getTargetIp().filter { filter }.collect {
                        filter = false

                        withContext(Dispatchers.Main)
                        {
                            if (it == ipFromRequest)
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
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(this@SyncService, "Reply request", Toast.LENGTH_SHORT).show()
                }

                if (isSyncStaticState)
                {
                    listenerEventArgs.callBack?.invoke(false)
                    return
                }

                // TODO: Implement the SecurityService to validate and save the code

                if (listenerEventArgs.message.toBoolean())
                {
                    CoroutineScope(Dispatchers.IO).launch {
                        var filter = true

                        dataStore.getTargetIp().filter { filter }.collect {
                            filter = false

                            if (it == listenerEventArgs.ip)
                            {
                                withContext(Dispatchers.Main) {
                                    isSyncFlow.value = true
                                    observeSyncState()
                                }
                            }

                        }
                    }
                }
            }

            ListenerEventType.Disconnect        ->
            {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(this@SyncService, "Disconnect", Toast.LENGTH_SHORT).show()
                }

                CoroutineScope(Dispatchers.IO).launch {
                    var filter = true

                    dataStore.getTargetIp().filter { filter }.collect {
                        filter = false

                        if (it == listenerEventArgs.ip)
                        {
                            withContext(Dispatchers.Main) {
                                dataStore.deleteTargetIp()
                                isSyncFlow.value = false
                            }
                        }

                    }
                }

            }

            ListenerEventType.ClipboardContent  ->
            {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(this@SyncService, "Clipboard content", Toast.LENGTH_SHORT).show()
                }

                clipboardService.setClipboard(listenerEventArgs.message!!)
            }
        }
    }
}