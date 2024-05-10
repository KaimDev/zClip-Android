package com.example.zclip_android

import com.example.zclip_android.services.ClipboardService
import com.example.zclip_android.services.SyncService

class Container
{
    fun resolveSyncService() : SyncService
    {
        return SyncService()
    }

    fun resolveClipboardService() : ClipboardService
    {
        return ClipboardService()
    }
}