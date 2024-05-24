package com.kaimdev.zclip_android.services

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.kaimdev.zclip_android.helpers.ClipboardModes
import com.kaimdev.zclip_android.interfaces.IClipboardService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.kaimdev.zclip_android.helpers.ServiceExtensions.Companion.sendNotification
import com.kaimdev.zclip_android.stores.DataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext

class ClipboardService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val clipboardManager: ClipboardManager,
    private val dataStore: DataStore
) :
    IClipboardService
{
    private var clipboardModes: ClipboardModes? = null
    private var taskHandler: Handler? = null
    private lateinit var runnable: Runnable
    private var lastContent: String? = null

    override fun start()
    {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "Clipboard service started", Toast.LENGTH_SHORT).show()
        }

        CoroutineScope(Dispatchers.IO).launch {

            getClipboardMode()

            withContext(Dispatchers.Default)
            {
                if (ClipboardModes.AUTOMATIC == clipboardModes)
                {
                    runAuto()
                }
            }
        }
    }

    override fun stop()
    {
        if (clipboardModes == ClipboardModes.AUTOMATIC)
        {
            taskHandler?.removeCallbacks(runnable)
        }
    }

    override fun getManualClipboard()
    {
        getClipboard()
    }

    override fun setClipboard(content: String)
    {
        val clip: ClipData = ClipData.newPlainText("content", content)
        clipboardManager.setPrimaryClip(clip)
    }

    private fun restart()
    {
        stop()

        if (ClipboardModes.AUTOMATIC == clipboardModes)
        {
            runAuto()
        }
    }

    private fun runAuto()
    {
        taskHandler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            getClipboard()
            taskHandler!!.postDelayed(runnable, 10000)
        }

        taskHandler!!.post(runnable)
    }

    private fun getClipboard()
    {
        val clipData = clipboardManager.primaryClip

        if (clipData != null && clipData.itemCount > 0)
        {
            val content = clipData.getItemAt(0).text.toString()
            if (lastContent == content)
            {
                return
            }
            lastContent = content
            sendNotification(lastContent!!)
        }
    }

    private suspend fun getClipboardMode()
    {
        dataStore.getClipboardMode().collect()
        {
            clipboardModes = it
            restart()
        }
    }
}