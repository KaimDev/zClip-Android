package com.kaimdev.zclip_android.event_args

import com.kaimdev.zclip_android.interfaces.IEventArgs

data class ListenerEventArgs(
    val listenerEvenType: ListenerEventType,
    val ip: String? = null,
    val code: String? = null,
    val message: String? = null,
    val callBack : ((Any) -> Unit)? = null
) : IEventArgs

enum class ListenerEventType
{
    RequestConnection,
    ReplyRequest,
    Disconnect,
    ClipboardContent
}