package com.example.zclip_android.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.zclip_android.interfaces.IObserver
import com.example.zclip_android.helpers.ServiceExtensions.Companion.subscribe
import javax.inject.Inject

class SyncService @Inject constructor(
    private val clipboardService: ClipboardService
) : Service(),
    IObserver
{
    init
    {
        clipboardService.subscribe(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun notify(message: String)
    {
        TODO("Not yet implemented")
    }

    override fun onBind(intent: Intent?): IBinder?
    {
        return null
    }
}