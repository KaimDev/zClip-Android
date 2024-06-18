package com.kaimdev.zclip_android.modules

import android.content.ClipboardManager
import android.content.Context
import com.kaimdev.zclip_android.helpers.NotificationBuilder
import com.kaimdev.zclip_android.models.ListenerSettingsModel
import com.kaimdev.zclip_android.models.LocalIpModel
import com.kaimdev.zclip_android.server.IApplicationApi
import com.kaimdev.zclip_android.services.ClientService
import com.kaimdev.zclip_android.services.ClipboardService
import com.kaimdev.zclip_android.services.ListenerService
import com.kaimdev.zclip_android.services.NotificationService
import com.kaimdev.zclip_android.stores.DataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ServicesModule
{
    @Provides
    fun provideClientService(
        client: IApplicationApi,
        localIpModel: LocalIpModel,
        @ApplicationContext context: Context
    ): ClientService
    {
        return ClientService(client, localIpModel, context)
    }

    @Provides
    fun provideClipboardService(
        clipboardManager: ClipboardManager,
        dataStore: DataStore,
        @ApplicationContext context: Context
    ): ClipboardService
    {
        return ClipboardService(clipboardManager, dataStore, context)
    }

    @Provides
    fun provideListenerService(
        listenerSettingsModel: ListenerSettingsModel,
        @ApplicationContext context: Context
    ): ListenerService
    {
        return ListenerService(listenerSettingsModel, context)
    }

    @Provides
    fun provideNotificationService(
        @ApplicationContext context: Context,
        dataStore: DataStore,
        notificationBuilder: NotificationBuilder
    ): NotificationService
    {
        return NotificationService(context, dataStore, notificationBuilder)
    }
}