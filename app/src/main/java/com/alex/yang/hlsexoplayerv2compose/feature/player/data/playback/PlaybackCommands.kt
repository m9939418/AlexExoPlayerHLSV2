package com.alex.yang.hlsexoplayerv2compose.feature.player.data.playback

import android.os.Bundle
import androidx.media3.session.SessionCommand

/**
 * Created by AlexYang on 2025/12/25.
 *
 *
 */
object PlaybackCommands {
    const val ACTION_SKIP_BACK_1S = "action.SKIP_BACK_1S"
    const val ACTION_SKIP_FORWARD_1S = "action.SKIP_FORWARD_1S"

    const val ACTION_SKIP_BACK_10S = "action.SKIP_BACK_10S"
    const val ACTION_SKIP_FORWARD_10S = "action.SKIP_FORWARD_10S"

    val skipPrev1s = SessionCommand(ACTION_SKIP_BACK_1S, Bundle.EMPTY)
    val skipNext1s = SessionCommand(ACTION_SKIP_FORWARD_1S, Bundle.EMPTY)

    val SkipBack10s = SessionCommand(ACTION_SKIP_BACK_10S, Bundle.EMPTY)

    val SkipForward10s = SessionCommand(ACTION_SKIP_FORWARD_10S, Bundle.EMPTY)
}
