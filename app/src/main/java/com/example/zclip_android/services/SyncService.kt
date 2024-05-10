package com.example.zclip_android.services

import com.example.zclip_android.helpers.Observer
import com.example.zclip_android.helpers.ServiceExtensions.Companion.subscribe

class SyncService(clipboardService: ClipboardService) : Observer
{
    init
    {
        clipboardService.subscribe(this)
    }

    override fun notify(message: String)
    {
        TODO("Not yet implemented")
    }
}