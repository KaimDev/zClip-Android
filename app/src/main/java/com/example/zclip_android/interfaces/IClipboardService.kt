package com.example.zclip_android.interfaces

import com.example.zclip_android.helpers.ClipboardModes

interface IClipboardService : IService
{
    fun start()
    fun stop()
    fun changeMode(mode: ClipboardModes)
}