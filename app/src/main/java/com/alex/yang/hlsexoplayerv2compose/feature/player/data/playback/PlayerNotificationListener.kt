package com.alex.yang.hlsexoplayerv2compose.feature.player.data.playback

import android.app.Notification
import android.app.Service.STOP_FOREGROUND_REMOVE
import androidx.media3.common.Player
import androidx.media3.ui.PlayerNotificationManager
import com.alex.yang.hlsexoplayerv2compose.feature.player.data.service.PlaybackService

/**
 * Created by AlexYang on 2025/12/25.
 *
 *
 */
class PlayerNotificationListener(
    private val service: PlaybackService,
    private val exoPlayer: Player
) : PlayerNotificationManager.NotificationListener {
    override fun onNotificationPosted(id: Int, notification: Notification, ongoing: Boolean) {
        service.startForeground(id, notification)
    }

    override fun onNotificationCancelled(id: Int, dismissedByUser: Boolean) {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()

        service.stopForeground(STOP_FOREGROUND_REMOVE)
        service.stopSelf()
    }
}