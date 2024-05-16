package com.example.zclip_android.di

import android.content.ClipboardManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ClipboardManagerModule
{
    @Singleton
    @Provides
    fun provideClipboardManager(@ApplicationContext context: Context): ClipboardManager
    {
        return context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }
}