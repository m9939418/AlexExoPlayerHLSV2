package com.alex.yang.hlsexoplayerv2compose

import android.app.Application
import com.alex.yang.hlsexoplayerv2compose.feature.player.data.notification.AppNotificationChannel
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Created by AlexYang on 2025/12/25.
 *
 *
 */
@HiltAndroidApp
class App: Application() {
    @Inject
    lateinit var appNotificationChannel: AppNotificationChannel

    override fun onCreate() {
        super.onCreate()

        appNotificationChannel.init()
    }
}