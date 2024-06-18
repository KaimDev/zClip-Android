package com.kaimdev.zclip_android.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.kaimdev.zclip_android.helpers.ClipboardModes
import com.kaimdev.zclip_android.interfaces.INotificationService
import com.kaimdev.zclip_android.stores.DataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.kaimdev.zclip_android.helpers.NotificationBuilder
import com.kaimdev.zclip_android.helpers.NotificationChannels

class NotificationService(
    private val context: Context,
    private val dataStore: DataStore,
    private val notificationBuilder: NotificationBuilder
) : INotificationService
{
    override fun start()
    {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(
                context,
                "Notification service started",
                Toast.LENGTH_SHORT
            ).show()
        }

        createChannels()
        observeClipboardModes()
    }

    override fun stop()
    {
        hideSendClipboardContentNotification()
    }

    private fun createChannels()
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            val sendClipboardContentChannel = NotificationChannel(
                NotificationChannels.SEND_CLIPBOARD_CONTENT,
                NotificationChannels.SEND_CLIPBOARD_CONTENT,
                NotificationManager.IMPORTANCE_LOW
            )

            val requestConnectionChannel = NotificationChannel(
                NotificationChannels.REQUEST_CONNECTION,
                NotificationChannels.REQUEST_CONNECTION,
                NotificationManager.IMPORTANCE_HIGH
            )

            with(NotificationManagerCompat.from(context))
            {
                createNotificationChannel(sendClipboardContentChannel)
                createNotificationChannel(requestConnectionChannel)
            }
        }
    }

    private fun observeClipboardModes()
    {
        CoroutineScope(Dispatchers.IO).launch {

            dataStore.getClipboardMode().collect {

                withContext(Dispatchers.Main)
                {
                    if (it == ClipboardModes.MANUAL)
                    {
                        showSendClipboardContentNotification()
                    } else
                    {
                        hideSendClipboardContentNotification()
                    }

                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun showSendClipboardContentNotification()
    {
        val notification =
            notificationBuilder.buildByChannelId(NotificationChannels.SEND_CLIPBOARD_CONTENT)

        with(NotificationManagerCompat.from(context)) {
            notify(1, notification!!)
        }
    }

    private fun hideSendClipboardContentNotification()
    {
        with(NotificationManagerCompat.from(context))
        {
            cancel(1)
        }
    }

    @SuppressLint("MissingPermission")
    override fun showRequestConnectionNotification()
    {
        val notification =
            notificationBuilder.buildByChannelId(NotificationChannels.REQUEST_CONNECTION)

        with(NotificationManagerCompat.from(context)) {
            notify(2, notification!!)
        }
    }

    override fun hideRequestConnectionNotification()
    {
        with(NotificationManagerCompat.from(context))
        {
            cancel(2)
        }
    }
}