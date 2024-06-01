package com.kaimdev.zclip_android.interfaces

import kotlinx.coroutines.flow.Flow

interface ISyncService
{
    fun isSync(): Flow<Boolean>
    fun start()
    fun stop()
    fun sendClipboardContent()
}