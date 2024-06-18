package com.kaimdev.zclip_android.interfaces

import com.kaimdev.zclip_android.helpers.SyncState
import kotlinx.coroutines.flow.Flow

interface ISyncService : IService
{
    fun isSync(): Flow<SyncState>
    fun start()
    fun stop()
    fun sendClipboardContent()
    fun allowConnection()
    fun denyConnection()
}