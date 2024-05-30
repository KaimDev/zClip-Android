package com.kaimdev.zclip_android.event_args

import com.kaimdev.zclip_android.interfaces.IEventArgs

data class ListenerEventArgs(
    val message : String,
    val listenerEvenType : ListenerEventType
) : IEventArgs

enum class ListenerEventType
{
    ConnectRequest,
    Disconnect,
    ClipboardContent
}