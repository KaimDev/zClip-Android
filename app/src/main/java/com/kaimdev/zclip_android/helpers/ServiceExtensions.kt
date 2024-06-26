package com.kaimdev.zclip_android.helpers

import com.kaimdev.zclip_android.interfaces.IEventArgs
import com.kaimdev.zclip_android.interfaces.IObserver
import com.kaimdev.zclip_android.interfaces.IService

class ServiceExtensions
{
    companion object
    {
        private val IService.IObservers: MutableList<IObserver> by lazy { mutableListOf<IObserver>() }

        fun IService.subscribe(observer: IObserver)
        {
            IObservers.add(observer)
        }

        fun IService.sendNotification(eventArgs: IEventArgs)
        {
            IObservers.forEach { it.notify(this, eventArgs) }
        }
    }
}