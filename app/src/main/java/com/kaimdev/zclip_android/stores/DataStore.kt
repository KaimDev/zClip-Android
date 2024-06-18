package com.kaimdev.zclip_android.stores

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kaimdev.zclip_android.helpers.ClipboardModes
import com.kaimdev.zclip_android.helpers.DataStoreKeys
import com.kaimdev.zclip_android.helpers.Languages
import com.kaimdev.zclip_android.helpers.DeviceThemes
import com.kaimdev.zclip_android.helpers.SystemSettingsProvider
import com.kaimdev.zclip_android.helpers.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class DataStore @Inject constructor(
    @ApplicationContext context: Context,
    private val systemSettingsProvider: SystemSettingsProvider
)
{
    private val dataStore = context.dataStore
    private var filterOne = true
    private var filterTwo = true
    private var filterThree = true

    fun start()
    {
        return
    }

    suspend fun initialConfiguration()
    {
        val clipboardModeFlow = getClipboardMode()
        val themeFlow = getTheme()
        val languageFlow = getLanguage()

        CoroutineScope(Dispatchers.IO).launch {

            clipboardModeFlow.filter { filterOne }.collect()
            {
                if (it == null)
                {
                    val androidVersion = systemSettingsProvider.getAndroidVersion()

                    if (androidVersion >= 29)
                    {
                        setClipboardMode(ClipboardModes.MANUAL)
                    }
                    else
                    {
                        setClipboardMode(ClipboardModes.AUTOMATIC)
                    }
                }

                filterOne = false
            }

            themeFlow.filter { filterTwo }.collect()
            {
                if (it == null)
                {
                    setTheme(systemSettingsProvider.getSystemTheme())
                }

                filterTwo = false
            }

            languageFlow.filter { filterThree }.collect()
            {
                if (it == null)
                {
                    setLanguage(systemSettingsProvider.getSystemLanguage())
                }

                filterThree = false
            }
        }
    }

    fun getClipboardMode(): Flow<ClipboardModes?>
    {
        return dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(DataStoreKeys.ITEM_CLIPBOARD_MODE)]?.let { clipboardMode ->
                ClipboardModes.valueOf(clipboardMode)
            }
        }
    }

    fun getTheme(): Flow<DeviceThemes?>
    {
        return dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(DataStoreKeys.ITEM_THEME)]?.let { theme ->
                DeviceThemes.valueOf(theme)
            }
        }
    }

    fun getLanguage(): Flow<Languages?>
    {
        return dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(DataStoreKeys.ITEM_LANGUAGE)]?.let { language ->
                Languages.valueOf(language)
            }
        }
    }

    fun getTargetIp(): Flow<String?>
    {
        return dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(DataStoreKeys.ITEM_TARGET_IP)]
        }
    }

    fun setClipboardMode(clipboardMode: ClipboardModes)
    {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { preferences ->
                preferences[stringPreferencesKey(DataStoreKeys.ITEM_CLIPBOARD_MODE)] =
                    clipboardMode.name
            }
        }
    }

    fun setTheme(theme: DeviceThemes)
    {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { preferences ->
                preferences[stringPreferencesKey(DataStoreKeys.ITEM_THEME)] = theme.name
            }
        }
    }

    fun setLanguage(language: Languages)
    {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { preferences ->
                preferences[stringPreferencesKey(DataStoreKeys.ITEM_LANGUAGE)] = language.name
            }
        }
    }

    fun setTargetIp(targetIp: String)
    {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { preferences ->
                preferences[stringPreferencesKey(DataStoreKeys.ITEM_TARGET_IP)] = targetIp
            }
        }
    }

    fun deleteTargetIp()
    {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { preferences ->
                preferences.remove(stringPreferencesKey(DataStoreKeys.ITEM_TARGET_IP))
            }
        }
    }
}