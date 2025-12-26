package com.alex.yang.hlsexoplayerv2compose.feature.player.data.playback

import android.os.Bundle
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

/**
 * Created by AlexYang on 2025/12/25.
 *
 *
 */
class MediaSessionCallback(private val exoPlayer: Player) : MediaSession.Callback {
    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): MediaSession.ConnectionResult {
        val connectionResult = super.onConnect(session, controller)

        // 註冊所有自訂命令
        val sessionCommands = connectionResult.availableSessionCommands
            .buildUpon()
            .add(PlaybackCommands.skipPrev1s)
            .add(PlaybackCommands.skipNext1s)
            .add(PlaybackCommands.SkipBack10s)
            .add(PlaybackCommands.SkipForward10s)
            .build()

        val playerCommands = connectionResult.availablePlayerCommands

        return MediaSession.ConnectionResult
            .accept(sessionCommands, playerCommands)
    }

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {
        when (customCommand.customAction) {
            PlaybackCommands.ACTION_SKIP_BACK_1S -> {
                val newPos = (exoPlayer.currentPosition - 1_000).coerceAtLeast(0)
                exoPlayer.seekTo(newPos)
            }

            PlaybackCommands.ACTION_SKIP_FORWARD_1S -> {
                val duration = exoPlayer.duration

                val newPos = if (duration > 0) {
                    (exoPlayer.currentPosition + 1_000).coerceAtMost(duration)
                } else {
                    exoPlayer.currentPosition + 1_000
                }
                exoPlayer.seekTo(newPos)
            }

            PlaybackCommands.ACTION_SKIP_BACK_10S -> {
                val newPos = (exoPlayer.currentPosition - 10_000).coerceAtLeast(0)
                exoPlayer.seekTo(newPos)
            }

            PlaybackCommands.ACTION_SKIP_FORWARD_10S -> {
                val duration = exoPlayer.duration

                val newPos = if (duration > 0) {
                    (exoPlayer.currentPosition + 10_000).coerceAtMost(duration)
                } else {
                    exoPlayer.currentPosition + 10_000
                }
                exoPlayer.seekTo(newPos)
            }

            else -> {
                return Futures.immediateFuture(
                    SessionResult(SessionResult.RESULT_ERROR_NOT_SUPPORTED)
                )
            }
        }
        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
    }
}