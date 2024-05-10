package com.example.zclip_android.services

import com.example.zclip_android.helpers.ClipboardModes
import com.example.zclip_android.interfaces.IClipboardService

class ClipboardService : IClipboardService
{
    private var clipboardModes: ClipboardModes = ClipboardModes.MANUAL

    override fun start()
    {
        TODO("Not yet implemented")
    }

    override fun stop()
    {
        TODO("Not yet implemented")
    }

    override fun changeMode(mode: ClipboardModes)
    {
        TODO("Not yet implemented")
    }

    private fun getClipboard()
    {
        TODO("Not yet implemented")

    }

    private fun setClipboard()
    {
        TODO("Not yet implemented")
    }
}