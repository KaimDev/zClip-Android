package com.example.zclip_android.interfaces

interface IObserver
{
    fun <TService : IService> notify(sender: TService, message: String)
}