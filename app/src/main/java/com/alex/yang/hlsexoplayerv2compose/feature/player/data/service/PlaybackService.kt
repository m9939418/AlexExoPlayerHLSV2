package com.alex.yang.hlsexoplayerv2compose.feature.player.data.service

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.alex.yang.hlsexoplayerv2compose.feature.player.data.notification.AppNotificationChannel.Companion.CHANNEL_ID_PLAYBACK
import com.alex.yang.hlsexoplayerv2compose.feature.player.data.playback.MediaDescriptionAdapter
import com.alex.yang.hlsexoplayerv2compose.feature.player.data.playback.MediaSessionCallback
import com.alex.yang.hlsexoplayerv2compose.feature.player.data.playback.PlayerNotificationListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Created by AlexYang on 2025/12/23.
 *
 *
 */
@UnstableApi
@AndroidEntryPoint
class PlaybackService : MediaSessionService() {
    @Inject lateinit var exoPlayer: ExoPlayer

    private var mediaSession: MediaSession? = null
    private var playerNotificationManager: PlayerNotificationManager? = null

    override fun onCreate() {
        super.onCreate()

        mediaSession = MediaSession.Builder(this, exoPlayer)
            .setCallback(MediaSessionCallback(exoPlayer))
            .build()

        playerNotificationManager = PlayerNotificationManager
            .Builder(this, NOTIFICATION_ID, CHANNEL_ID_PLAYBACK)
            .setNotificationListener(PlayerNotificationListener(this, exoPlayer))
            .setMediaDescriptionAdapter(MediaDescriptionAdapter(this))
            .build().apply {
                setPlayer(exoPlayer)
                mediaSession?.let { setMediaSessionToken(it.platformToken) }
            }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        playerNotificationManager?.setPlayer(null)
        mediaSession?.release()
        exoPlayer.release()
        super.onDestroy()
    }

    companion object {
        const val NOTIFICATION_ID = 1
    }
}
