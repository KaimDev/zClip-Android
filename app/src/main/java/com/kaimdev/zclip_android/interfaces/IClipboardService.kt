package com.kaimdev.zclip_android.interfaces

interface IClipboardService : IService
{
    fun start()
    fun stop()
    fun getManualClipboard()
    fun setClipboard(content: String)
}