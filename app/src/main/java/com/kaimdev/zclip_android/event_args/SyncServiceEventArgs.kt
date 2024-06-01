package com.kaimdev.zclip_android.event_args

import com.kaimdev.zclip_android.interfaces.IEventArgs

data class SyncServiceEventArgs (
    val showRequestConnectionDialog : Boolean
) : IEventArgs