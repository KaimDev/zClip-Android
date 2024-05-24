package com.kaimdev.zclip_android.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.widget.Toast
import com.kaimdev.zclip_android.interfaces.IObserver
import com.kaimdev.zclip_android.helpers.ServiceExtensions.Companion.subscribe
import com.kaimdev.zclip_android.interfaces.IClipboardService
import com.kaimdev.zclip_android.interfaces.IService
import com.kaimdev.zclip_android.interfaces.ISyncService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SyncService : Service(), ISyncService, IObserver
{
    @Inject
    lateinit var clipboardService: ClipboardService

    private val binder = LocalBinder()

    private var isSync = false

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
    }

    override fun isSync(): Boolean
    {
        return isSync
    }

    override fun start()
    {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(this@SyncService, "Sync service started", Toast.LENGTH_SHORT).show()
        }
        clipboardService.start()
        isSync = true
    }

    override fun stop()
    {
        clipboardService.stop()
        isSync = false
    }

    override fun sendClipboardContent()
    {
        clipboardService.getManualClipboard()
    }

    override fun <TService : IService> notify(sender: TService, message: String)
    {
        when (sender)
        {
            is IClipboardService ->
            {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder
    {
        return binder
    }
}