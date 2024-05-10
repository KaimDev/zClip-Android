package com.example.zclip_android

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class MainActivityViewModel(application: Application) : AndroidViewModel(application)
{
    private val container = Container()

    fun resolveSyncService() = container.resolveSyncService()
}