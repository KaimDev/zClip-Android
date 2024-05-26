package com.kaimdev.zclip_android.helpers

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.kaimdev.zclip_android.MainActivity
import com.kaimdev.zclip_android.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class NotificationBuilder @Inject constructor(@ApplicationContext private val context: Context)
{
    fun buildByChannelId(
        channelId: String
    ): Notification?
    {
        when (channelId)
        {
            NotificationChannels.SEND_CLIPBOARD_CONTENT ->
            {
                return sendClipboardContentBuilder()
            }
        }

        return null
    }

    private fun sendClipboardContentBuilder(): Notification
    {
        val channelId = NotificationChannels.SEND_CLIPBOARD_CONTENT

        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification", true)
        }

        val mainPendingIntent =
            PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.click_to_send_the_clipboard_content))
            .setContentIntent(mainPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }
}