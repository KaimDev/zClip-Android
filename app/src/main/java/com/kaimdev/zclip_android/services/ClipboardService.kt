package com.kaimdev.zclip_android.services

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.datastore.preferences.core.intPreferencesKey
import com.kaimdev.zclip_android.helpers.ClipboardModes
import com.kaimdev.zclip_android.helpers.DataStoreKeys
import com.kaimdev.zclip_android.helpers.dataStore
import com.kaimdev.zclip_android.interfaces.IClipboardService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.kaimdev.zclip_android.helpers.ServiceExtensions.Companion.sendNotification

class ClipboardService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val clipboardManager: ClipboardManager
) :
    IClipboardService
{
    private lateinit var clipboardModes: ClipboardModes
    private lateinit var taskHandler: Handler
    private lateinit var runnable: Runnable
    private lateinit var lastContent: String

    override fun start()
    {
        CoroutineScope(Dispatchers.IO).launch {
            getClipboardMode()
        }

        if (ClipboardModes.AUTOMATIC == clipboardModes)
        {
            taskHandler = Handler(Looper.getMainLooper())
            runnable = Runnable {
                getClipboard()
                taskHandler.postDelayed(runnable, 10000)
            }

            taskHandler.post(runnable)
        }
    }

    override fun stop()
    {
        if (clipboardModes == ClipboardModes.AUTOMATIC)
        {
            taskHandler.removeCallbacks(runnable)
        }
    }

    override fun getManualClipboard()
    {
        getClipboard()
    }

    private suspend fun getClipboardMode()
    {
        context.dataStore.data.map { preferences ->
            (preferences[intPreferencesKey(DataStoreKeys.ITEM_CLIPBOARD_MODE)]) as ClipboardModes?
        }.also {
            var firstTime = true

            it.filter { firstTime }.collect { mode ->
                clipboardModes = mode as ClipboardModes
                stop()
                start()
                firstTime = !firstTime
            }
        }
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
            sendNotification(lastContent)
        }
    }

    private fun setClipboard(content: String)
    {
        val clip: ClipData = ClipData.newPlainText("content", content)
        clipboardManager.setPrimaryClip(clip)
    }
}