package com.kaimdev.zclip_android

import android.app.Application
import android.app.Instrumentation
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

class Runner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: android.content.Context?
    ): Application? = Instrumentation.newApplication(HiltTestApplication::class.java, context)
}
