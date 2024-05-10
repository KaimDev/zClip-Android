package com.example.zclip_android.services

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.example.zclip_android.MainActivity
import com.example.zclip_android.helpers.ClipboardModes
import com.example.zclip_android.interfaces.IClipboardService

class ClipboardService : IClipboardService
{
    private var clipboardModes: ClipboardModes = ClipboardModes.MANUAL

    private lateinit var clipboardManager : ClipboardManager;

    init
    {
        val context = MainActivity.applicationContext()

        clipboardManager  = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

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

    private fun getClipboard() : String?
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