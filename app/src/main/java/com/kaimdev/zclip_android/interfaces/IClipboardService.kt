package com.kaimdev.zclip_android.interfaces

import com.kaimdev.zclip_android.helpers.ClipboardModes

interface IClipboardService : IService
{
    fun start()
    fun stop()
    fun getManualClipboard()
}