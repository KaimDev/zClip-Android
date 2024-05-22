package com.kaimdev.zclip_android

import com.kaimdev.zclip_android.helpers.ClipboardModes
import com.kaimdev.zclip_android.helpers.ServiceExtensions.Companion.subscribe
import com.kaimdev.zclip_android.interfaces.IObserver
import com.kaimdev.zclip_android.interfaces.IService
import com.kaimdev.zclip_android.services.ClipboardService
import com.kaimdev.zclip_android.stores.DataStore
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class ClipboardServiceTests
{
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var dataStore: DataStore

    @Inject
    lateinit var clipboardService: ClipboardService

    @Before
    fun init()
    {
        hiltRule.inject()
    }

    // TODO: Fix the test
    @Test
    fun testGetTheClipboardByManualMode() = runBlocking {
        // Arrange
        var result: String? = null
        val observer = object : IObserver
        {
            override fun <TService : IService> notify(sender: TService, message: String)
            {
                if (sender is ClipboardService)
                {
                    result = message
                }
            }
        }

        val text = "Lorem ipsum dolor sit amet"

        // Act
        clipboardService.subscribe(observer)
        delay(3000L)

        dataStore.setClipboardMode(ClipboardModes.MANUAL)
        delay(3000L)

        clipboardService.setClipboard(text)
        delay(3000L)

        clipboardService.getManualClipboard()
        delay(3000L)

        // Assert
        assert(result != null && result == text)
    }
}