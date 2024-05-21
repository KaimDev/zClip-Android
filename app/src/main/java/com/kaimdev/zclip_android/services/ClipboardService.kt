package com.kaimdev.zclip_android.services

import android.content.ClipData
import android.content.ClipboardManager
import com.kaimdev.zclip_android.helpers.ClipboardModes
import com.kaimdev.zclip_android.interfaces.IClipboardService
import javax.inject.Inject

class ClipboardService @Inject constructor(private val clipboardManager: ClipboardManager) :
    IClipboardService
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

    private fun getClipboard(): String?
    {
        val clipData = clipboardManager.primaryClip

        if (clipData != null && clipData.itemCount > 0)
        {
            return clipData.getItemAt(0).text.toString()
        }

        return null
    }

    private fun setClipboard(content: String)
    {
        val clip: ClipData = ClipData.newPlainText("content", content)
        clipboardManager.setPrimaryClip(clip)
    }
}