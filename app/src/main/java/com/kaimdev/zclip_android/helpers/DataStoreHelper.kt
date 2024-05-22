package com.kaimdev.zclip_android.helpers

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

object DataStoreKeys
{
    const val DATA_STORE_SETTINGS = "data_store_settings"
    const val ITEM_THEME = "item_theme"
    const val ITEM_LANGUAGE = "item_language"
    const val ITEM_CLIPBOARD_MODE = "item_clipboard_mode"
}

val Context.dataStore: DataStore<Preferences>
        by preferencesDataStore(name = DataStoreKeys.DATA_STORE_SETTINGS)
