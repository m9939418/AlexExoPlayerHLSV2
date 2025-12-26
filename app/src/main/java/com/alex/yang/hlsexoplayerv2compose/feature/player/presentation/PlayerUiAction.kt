package com.alex.yang.hlsexoplayerv2compose.feature.player.presentation

/**
 * Created by AlexYang on 2025/12/25.
 *
 *
 */
sealed interface PlayerUiAction {
    data object InitialPlayer: PlayerUiAction

    data object StartPlayerBar: PlayerUiAction
    data object EndPlayerBar: PlayerUiAction

    data object TogglePlayPause : PlayerUiAction
    data class SeekTo(val positionMs: Long, val fromUser: Boolean = true) : PlayerUiAction
    data object SkipPrev1s : PlayerUiAction
    data object SkipPrev10s : PlayerUiAction
    data object SkipNext1s : PlayerUiAction
    data object SkipNext10s : PlayerUiAction
}