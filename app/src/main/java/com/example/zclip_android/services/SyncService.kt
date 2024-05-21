package com.example.zclip_android.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.zclip_android.interfaces.IObserver
import com.example.zclip_android.helpers.ServiceExtensions.Companion.subscribe
import com.example.zclip_android.interfaces.IClipboardService
import com.example.zclip_android.interfaces.IService
import javax.inject.Inject

class SyncService @Inject constructor(
    private val clipboardService: ClipboardService
) : Service(),
    IObserver
{
    private val binder = LocalBinder()

    inner class LocalBinder : Binder()
    {
        fun getService(): SyncService
        {
            return this@SyncService
        }
    }

    init
    {
        clipboardService.subscribe(this)
    }

    override fun <TService : IService> notify(sender: TService, message: String)
    {
        when (sender)
        {
            is IClipboardService -> {}
        }
    }

    override fun onBind(intent: Intent?): IBinder
    {
        return binder
    }
}