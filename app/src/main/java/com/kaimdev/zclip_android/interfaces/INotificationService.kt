package com.kaimdev.zclip_android.interfaces

interface INotificationService : IService
{
    fun start()
    fun stop()
    fun showRequestConnectionNotification()
    fun hideRequestConnectionNotification()
}