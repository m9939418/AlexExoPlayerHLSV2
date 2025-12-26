package com.alex.yang.hlsexoplayerv2compose.feature.player.data.playback

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.media3.common.Player
import androidx.media3.ui.PlayerNotificationManager
import com.alex.yang.hlsexoplayerv2compose.MainActivity

/**
 * Created by AlexYang on 2025/12/25.
 *
 *
 */
class MediaDescriptionAdapter(
    private val context: Context
) : PlayerNotificationManager.MediaDescriptionAdapter {

    override fun getCurrentContentTitle(player: Player) = player.mediaMetadata.title?.toString().orEmpty()

    override fun getCurrentContentText(player: Player) = player.mediaMetadata.artist?.toString()

    override fun getCurrentLargeIcon(player: Player, callback: PlayerNotificationManager.BitmapCallback): Bitmap? = null

    override fun createCurrentContentIntent(player: Player): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}