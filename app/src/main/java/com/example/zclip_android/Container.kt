package com.example.zclip_android

import com.example.zclip_android.services.ClipboardService
import com.example.zclip_android.services.SyncService

class Container
{
    fun resolveSyncService() : SyncService
    {
        val clipboard = resolveClipboardService()
        return SyncService(clipboard)
    }

    private fun resolveClipboardService() : ClipboardService
    {
        return ClipboardService()
    }
}