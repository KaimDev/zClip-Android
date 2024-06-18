package com.kaimdev.zclip_android.services

import android.content.Context
import android.widget.Toast
import com.kaimdev.zclip_android.interfaces.IClientService
import com.kaimdev.zclip_android.models.LocalIpModel
import com.kaimdev.zclip_android.server.ClipboardContentDto
import com.kaimdev.zclip_android.server.IApplicationApi
import com.kaimdev.zclip_android.server.IpCodeParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClientService(
    private val client: IApplicationApi,
    private val localIpModel: LocalIpModel,
    private val context: Context
) :
    IClientService
{
    private lateinit var params: IpCodeParams

    override fun setCode(code: String)
    {
        params = IpCodeParams(localIpModel.ip, code)
    }

    override fun sendClipboardContent(content: String)
    {
        CoroutineScope(Dispatchers.IO).launch {
            val clipboardContentDto = ClipboardContentDto(content)
            client.sendClipboardContent(clipboardContentDto, params)
        }
    }

    override fun requestConnection()
    {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(
                context,
                "Requesting connection",
                Toast.LENGTH_SHORT
            ).show()
        }

        CoroutineScope(Dispatchers.IO).launch {
            client.requestConnection(params)
        }
    }

    override fun replyConnection(allow: Boolean)
    {
        CoroutineScope(Dispatchers.IO).launch {
            client.replyConnection(localIpModel.ip, allow)
        }
    }

    override fun disconnect()
    {
        CoroutineScope(Dispatchers.IO).launch {
            client.disconnect(params)
        }
    }
}