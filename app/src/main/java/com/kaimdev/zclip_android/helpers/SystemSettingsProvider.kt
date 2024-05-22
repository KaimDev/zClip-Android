package com.kaimdev.zclip_android.helpers

import android.app.UiModeManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SystemSettingsProvider @Inject constructor(@ApplicationContext private val context: Context)
{
    fun getSystemTheme(): DeviceThemes
    {
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        return when (uiModeManager.nightMode)
        {
            UiModeManager.MODE_NIGHT_YES -> DeviceThemes.DARK
            UiModeManager.MODE_NIGHT_NO -> DeviceThemes.LIGHT
            else -> DeviceThemes.SYSTEM
        }
    }

    fun getSystemLanguage(): Languages
    {
        return when (Locale.getDefault().language)
        {
            "es" -> Languages.SPANISH
            else -> Languages.ENGLISH
        }
    }
}