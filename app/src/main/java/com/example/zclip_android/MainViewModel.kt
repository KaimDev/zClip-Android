package com.example.zclip_android

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class MainViewModel(application: Application) : AndroidViewModel(application)
{
    private val container = Container()

    private val syncService = container.resolveSyncService()
}