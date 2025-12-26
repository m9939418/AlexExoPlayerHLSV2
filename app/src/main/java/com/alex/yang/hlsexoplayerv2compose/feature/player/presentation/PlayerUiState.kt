package com.alex.yang.hlsexoplayerv2compose.feature.player.presentation

import com.alex.yang.hlsexoplayerv2compose.feature.player.data.model.PlaybackNotificationData

/**
 * Created by AlexYang on 2025/11/29.
 *
 *
 */
data class PlayerUiState(
    val data: PlaybackNotificationData = PlaybackNotificationData(),
    val currentMills: Long = 0L,
    val totalMills: Long = 0L,
    val isPlaying: Boolean = false,
    val error: String? = null
)