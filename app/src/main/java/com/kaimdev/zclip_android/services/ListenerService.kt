package com.kaimdev.zclip_android.services

import android.content.Context
import android.widget.Toast
import com.kaimdev.zclip_android.event_args.ListenerEventArgs
import com.kaimdev.zclip_android.event_args.ListenerEventType
import com.kaimdev.zclip_android.helpers.ServiceExtensions.Companion.sendNotification
import com.kaimdev.zclip_android.interfaces.IListenerService
import com.kaimdev.zclip_android.models.ListenerSettingsModel
import fi.iki.elonen.NanoHTTPD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListenerService(
    listenerSettingsModel: ListenerSettingsModel,
    private val context: Context
) :
    NanoHTTPD(
        listenerSettingsModel.port,
    ), IListenerService
{


    override fun start()
    {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(
                context,
                "Server started at 1705",
                Toast.LENGTH_SHORT
            ).show()
        }

        CoroutineScope(Dispatchers.IO).launch {
            super.start()
        }
    }

    override fun serve(session: IHTTPSession?): Response
    {
        val ip = session?.parameters?.get("ip")?.first()
            ?: return newFixedLengthResponse(
                Response.Status.BAD_REQUEST,
                "text/plain",
                "No ip received"
            )

        val uri = session.uri
        val method = session.method

        if (uri == "/reply_request" && method == Method.GET)
            return replyRequest(session, ip)

        val code = session.parameters?.get("code")?.first()
            ?: return newFixedLengthResponse(
                Response.Status.BAD_REQUEST,
                "text/plain",
                "No code received"
            )

        when (uri)
        {
            "/"                   ->
            {
                if (method == Method.POST)
                    return receiveClipboardContent(session)
            }

            "/request_connection" ->
            {
                if (method == Method.POST)
                    return requestConnection(ip, code)
            }

            "/disconnect"         ->
            {
                if (method == Method.GET)
                    return requestDisconnect(ip, code)
            }

            else                  ->
            {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not found")
            }
        }

        return super.serve(session)
    }

    private fun requestConnection(ip: String, code: String): Response
    {
        sendNotification(ListenerEventArgs(ListenerEventType.RequestConnection, ip, code))

        return newFixedLengthResponse(Response.Status.OK, "text/plain", "Request received")
    }

    private fun replyRequest(session: IHTTPSession, ip: String): Response
    {
        val message = session.parameters?.get("allow")?.first()
            ?: return newFixedLengthResponse(
                Response.Status.BAD_REQUEST,
                "text/plain",
                "No allow param received"
            )

        var canBeSync = false

        val callBack = { result: Any -> canBeSync = result as Boolean }

        sendNotification(
            ListenerEventArgs(
                ListenerEventType.ReplyRequest, ip, message = message, callBack = callBack
            )
        )

        if (!canBeSync)
            return newFixedLengthResponse(Response.Status.FORBIDDEN, "text/plain", "Request denied")

        return newFixedLengthResponse(Response.Status.OK, "text/plain", "Reply received")
    }

    private fun receiveClipboardContent(session: IHTTPSession): Response
    {
        val files = HashMap<String, String>()
        session.parseBody(files)

        val postData = files["postData"]
            ?: return newFixedLengthResponse(
                Response.Status.BAD_REQUEST,
                "text/plain",
                "No content received"
            )

        CoroutineScope(Dispatchers.Main).launch {
            sendNotification(ListenerEventArgs(ListenerEventType.ClipboardContent, postData))
        }

        return newFixedLengthResponse(
            Response.Status.ACCEPTED,
            "text/plain",
            "Clipboard content received"
        )
    }

    private fun requestDisconnect(ip: String, code: String): Response
    {
        sendNotification(ListenerEventArgs(ListenerEventType.Disconnect, ip, code))
        return newFixedLengthResponse(Response.Status.OK, "text/plain", "Disconnected")
    }
}
