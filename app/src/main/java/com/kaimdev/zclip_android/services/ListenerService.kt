package com.kaimdev.zclip_android.services

import com.kaimdev.zclip_android.helpers.ServiceExtensions.Companion.sendNotification
import com.kaimdev.zclip_android.interfaces.IListenerService
import com.kaimdev.zclip_android.models.ListenerSettingsModel
import fi.iki.elonen.NanoHTTPD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ListenerService @Inject constructor(listenerSettingsModel: ListenerSettingsModel) :
    NanoHTTPD(listenerSettingsModel.port), IListenerService
{
    override fun start()
    {
        CoroutineScope(Dispatchers.IO).launch {
            super.start()
        }
    }

    override fun serve(session: IHTTPSession?): Response
    {
        val uri = session?.uri

        when (uri)
        {
            "/"  ->
            {
                val method = session.method

                if (method == Method.POST)
                {
                    return receiveClipboardContent(session)
                } else if (method == Method.GET)
                {
                    return testConnection(session)
                }
            }

            else ->
            {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not found")
            }
        }

        return super.serve(session)
    }

    private fun receiveClipboardContent(session: IHTTPSession?): Response
    {
        val files = HashMap<String, String>()
        session?.parseBody(files)

        val postData = files["postData"]
            ?: return newFixedLengthResponse(
                Response.Status.BAD_REQUEST,
                "text/plain",
                "No content received"
            )

        sendNotification(postData)

        return newFixedLengthResponse(
            Response.Status.ACCEPTED,
            "text/plain",
            "Clipboard content received"
        )
    }

    private fun testConnection(session: IHTTPSession?): Response
    {
        return newFixedLengthResponse(Response.Status.OK, "text/plain", "Connection successful")
    }
}