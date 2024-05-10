package com.example.zclip_android.helpers

import com.example.zclip_android.interfaces.IService

class ServiceExtensions
{
    companion object
    {
        private val IService.observers: MutableList<Observer> by lazy { mutableListOf<Observer>() }

        fun IService.subscribe(observer: Observer)
        {
            observers.add(observer)
        }

        fun IService.sendNotification(message: String)
        {
            observers.forEach { it.notify(message) }
        }
    }
}