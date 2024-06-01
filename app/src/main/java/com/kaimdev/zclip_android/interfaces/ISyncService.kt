package com.kaimdev.zclip_android.interfaces

import kotlinx.coroutines.flow.Flow

interface ISyncService : IService
{
    fun isSync(): Flow<Boolean>
    fun start()
    fun stop()
    fun sendClipboardContent()
    fun allowConnection()
    fun denyConnection()
}