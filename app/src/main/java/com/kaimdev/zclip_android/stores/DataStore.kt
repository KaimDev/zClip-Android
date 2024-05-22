package com.kaimdev.zclip_android.stores

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DataStore @Inject constructor(
    @ApplicationContext context: Context,
    private val systemSettingsProvider: SystemSettingsProvider
)
{
    private val dataStore = context.dataStore

    suspend fun initialConfiguration()
    {
        val clipboardMode = getClipboardMode()
        val theme = getTheme()
        val language = getLanguage()

        CoroutineScope(Dispatchers.IO).launch {
            if (clipboardMode.last() == null)
            {
                setClipboardMode(ClipboardModes.MANUAL)
            }

            if (theme.last() == null)
            {
                setTheme(systemSettingsProvider.getSystemTheme())
            }

            if (language.last() == null)
            {
                setLanguage(systemSettingsProvider.getSystemLanguage())
            }
        }
    }

    fun getClipboardMode(): Flow<ClipboardModes?>
    {
        return dataStore.data.map { preferences ->
            preferences[intPreferencesKey(DataStoreKeys.ITEM_CLIPBOARD_MODE)] as ClipboardModes?
        }
    }

    fun getTheme(): Flow<DeviceThemes?>
    {
        return dataStore.data.map { preferences ->
            preferences[intPreferencesKey(DataStoreKeys.ITEM_THEME)] as DeviceThemes?
        }
    }

    fun getLanguage(): Flow<Languages?>
    {
        return dataStore.data.map { preferences ->
            preferences[intPreferencesKey(DataStoreKeys.ITEM_LANGUAGE)] as Languages?
        }
    }

    fun setClipboardMode(clipboardMode: ClipboardModes)
    {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { preferences ->
                preferences[intPreferencesKey(DataStoreKeys.ITEM_CLIPBOARD_MODE)] =
                    clipboardMode.ordinal
            }
        }
    }

    fun setTheme(theme: DeviceThemes)
    {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { preferences ->
                preferences[intPreferencesKey(DataStoreKeys.ITEM_THEME)] = theme.ordinal
            }
        }
    }

    fun setLanguage(language: Languages)
    {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { preferences ->
                preferences[intPreferencesKey(DataStoreKeys.ITEM_LANGUAGE)] = language.ordinal
            }
        }
    }
}