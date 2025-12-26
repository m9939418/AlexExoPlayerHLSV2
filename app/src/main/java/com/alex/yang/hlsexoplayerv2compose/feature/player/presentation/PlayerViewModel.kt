package com.alex.yang.hlsexoplayerv2compose.feature.player.presentation

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionCommand
import com.alex.yang.hlsexoplayerv2compose.feature.player.data.model.PlaybackNotificationData
import com.alex.yang.hlsexoplayerv2compose.feature.player.data.playback.MediaControllerManager
import com.alex.yang.hlsexoplayerv2compose.feature.player.data.playback.PlaybackCommands
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

/**
 * Created by AlexYang on 2025/11/28.
 *
 *
 */
@OptIn(UnstableApi::class)
@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val mediaControllerManager: MediaControllerManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState = _uiState.asStateFlow()

    private val _controller = MutableStateFlow<MediaController?>(null)
    val controller = _controller.asStateFlow()

    private val initializationMutex = Mutex()
    private var hasInitialized = false
    private var progressJob: Job? = null

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _uiState.update { it.copy(isPlaying = isPlaying) }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            val controller = _controller.value ?: return

            when (playbackState) {
                Player.STATE_READY -> {
                    // READY 時更新 duration（避免 totalMills 長期為 0）
                    val duration = controller.duration
                    if (duration > 0) {
                        _uiState.update { it.copy(totalMills = duration) }
                    }
                }

                Player.STATE_ENDED -> {
                    controller.seekTo(0)
                    controller.pause()

                    _uiState.update {
                        it.copy(isPlaying = false, currentMills = 0L)
                    }
                }

                else -> Unit
            }
        }

        override fun onEvents(player: Player, events: Player.Events) {
            val controller = _controller.value ?: return

            val duration = controller.duration
            if (duration > 0 && _uiState.value.totalMills != duration) {
                _uiState.update { it.copy(totalMills = duration) }
            }
        }
    }

    fun onAction(action: PlayerUiAction) = when (action) {
        is PlayerUiAction.InitialPlayer -> initialPlayer()
        is PlayerUiAction.SeekTo -> seekTo(action.positionMs)
        is PlayerUiAction.StartPlayerBar -> startProgressTracker()
        is PlayerUiAction.EndPlayerBar -> stopProgressTracker()
        is PlayerUiAction.SkipNext1s -> sendCustom(PlaybackCommands.skipNext1s)
        is PlayerUiAction.SkipNext10s -> sendCustom(PlaybackCommands.SkipForward10s)
        is PlayerUiAction.SkipPrev1s -> sendCustom(PlaybackCommands.skipPrev1s)
        is PlayerUiAction.SkipPrev10s -> sendCustom(PlaybackCommands.SkipBack10s)
        is PlayerUiAction.TogglePlayPause -> togglePlayPause()
    }

    private fun initialPlayer() = viewModelScope.launch {
        initializationMutex.withLock {
            if (hasInitialized) {
                Log.d("DEBUG", "Already initialized, skipping")
                return@launch
            }
            hasInitialized = true
            Log.d("DEBUG", "Initializing player")

            runCatching {
                val controller = mediaControllerManager.get()
                _controller.value?.removeListener(playerListener)
                controller.addListener(playerListener)
                _controller.value = controller

                _uiState.update {
                    it.copy(
                        isPlaying = controller.isPlaying,
//                        currentMills = controller.currentPosition,
//                        totalMills = controller.duration.takeIf { d -> d > 0 } ?: 0L
                    )
                }
            }.onFailure { exception ->
                hasInitialized = false
                _uiState.update { it.copy(error = exception.message) }
                Log.e("DEBUG", "Init Player failed", exception)
            }
        }
    }

    private fun togglePlayPause() = viewModelScope.launch {
        runCatching {
            val controller = mediaControllerManager.get()

            if (controller.isPlaying) {
                controller.pause()
                return@launch
            }

            if (controller.currentMediaItem == null) {
                controller.setMediaItem(buildMediaItem(_uiState.value.data))
                controller.prepare()
            }

            controller.play()
        }.onFailure { exception ->
            _uiState.update {
                it.copy(error = exception.message, isPlaying = false)
            }
        }
    }

    private fun seekTo(positionMs: Long) = viewModelScope.launch {
        runCatching {
            mediaControllerManager.get().seekTo(positionMs.coerceAtLeast(0))
        }.onFailure { Log.e("DEBUG", "Seek failed", it) }
    }

    private fun sendCustom(command: SessionCommand) = viewModelScope.launch {
        runCatching {
            mediaControllerManager.get().sendCustomCommand(command, Bundle())
        }.onFailure { Log.e("DEBUG", "Command failed", it) }
    }

    private fun buildMediaItem(d: PlaybackNotificationData): MediaItem =
        MediaItem.Builder()
            .setUri(d.url)
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(d.title)
                    .setArtist(d.artist)
                    .setArtworkUri(Uri.parse(d.artwork))
                    .build()
            )
            .build()

    private fun startProgressTracker() {
        Log.d("DEBUG", "Starting progress tracker")
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (isActive) {
                runCatching {
                    val controller = _controller.value ?: return@runCatching

                    val currentPos = controller.currentPosition
                    val duration = controller.duration

                    _uiState.update {
                        it.copy(
                            currentMills = currentPos,
                            totalMills = if (duration > 0) duration else it.totalMills
                        )
                    }
                }.onFailure {
                    Log.w("DEBUG", "Progress update failed", it)
                }

                delay(200L)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    override fun onCleared() {
        stopProgressTracker()
        _controller.value?.removeListener(playerListener)
        mediaControllerManager.release()
        super.onCleared()
    }
}
