package com.kaimdev.zclip_android.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kaimdev.zclip_android.services.SyncService

class RequestConnectionReceiver : BroadcastReceiver()
{
    override fun onReceive(context: Context?, intent: Intent?)
    {
        val isAccept = intent?.getBooleanExtra("accept" , false)

        val syncService = Intent(context, SyncService::class.java)

        if (isAccept!!)
        {
            syncService.apply {
                action = SyncService.ACTION_ACCEPT_REQUEST_CONNECTION.toString()
            }
        } else
        {
            syncService.apply {
                action = SyncService.ACTION_DENY_REQUEST_CONNECTION.toString()
            }
        }

        context?.startService(syncService)
    }
}