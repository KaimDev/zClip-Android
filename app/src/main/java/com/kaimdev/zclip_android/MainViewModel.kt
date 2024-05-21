package com.kaimdev.zclip_android

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.kaimdev.zclip_android.services.SyncService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val syncService: SyncService
) : AndroidViewModel(application)
{

}