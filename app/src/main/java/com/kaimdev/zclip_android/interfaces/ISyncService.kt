package com.kaimdev.zclip_android.interfaces

interface ISyncService
{
    fun isSync() : Boolean
    fun start()
    fun stop()
    fun sendClipboardContent()
}