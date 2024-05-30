package com.kaimdev.zclip_android.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.kaimdev.zclip_android.event_args.ListenerEventArgs
import com.kaimdev.zclip_android.interfaces.IObserver
import com.kaimdev.zclip_android.helpers.ServiceExtensions.Companion.subscribe
import com.kaimdev.zclip_android.interfaces.IClipboardService
import com.kaimdev.zclip_android.interfaces.IEventArgs
import com.kaimdev.zclip_android.interfaces.IListenerService
import com.kaimdev.zclip_android.interfaces.IService
import com.kaimdev.zclip_android.interfaces.ISyncService
import dagger.hilt.android.AndroidEntryPoint
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

    private val binder = LocalBinder()

    private var isSync = false

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

    override fun isSync(): Boolean
    {
        return isSync
    }

    override fun start()
    {
        clipboardService.start()
        notificationService.start()
        listenerService.start()

        isSync = true
    }

    override fun stop()
    {
        clipboardService.stop()
        notificationService.stop()
        listenerService.stop()

        isSync = false
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

            is IListenerService ->
            {
                handleListenerService(eventArgs as ListenerEventArgs)
            }
        }
    }

    private fun handleListenerService(listenerEventArgs: ListenerEventArgs)
    {
    }
}