package com.kaimdev.zclip_android.interfaces

interface IClientService : IService
{
    fun setCode(code: String)
    fun sendClipboardContent(content: String)
    fun requestConnection()
    fun replyConnection(allow: Boolean)
    fun disconnect()
}