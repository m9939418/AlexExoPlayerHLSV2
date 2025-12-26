package com.alex.yang.hlsexoplayerv2compose.feature.player.presentation

import android.content.res.Configuration
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.ui.PlayerView
import com.alex.yang.hlsexoplayerv2compose.feature.player.data.model.PlaybackNotificationData
import com.alex.yang.hlsexoplayerv2compose.feature.player.presentation.component.MediaInfo
import com.alex.yang.hlsexoplayerv2compose.feature.player.presentation.component.PlayerAction
import com.alex.yang.hlsexoplayerv2compose.feature.player.presentation.component.PlayerSlider
import com.alex.yang.hlsexoplayerv2compose.ui.theme.AlexHLSExoPlayerV2ComposeTheme

/**
 * Created by AlexYang on 2025/11/28.
 *
 *
 */
@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    modifier: Modifier = Modifier,
    state: PlayerUiState,
    controller: MediaController?,
    onAction: (PlayerUiAction) -> Unit = {},
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val playerViewRef = remember { mutableStateOf<PlayerView?>(null) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    onAction(PlayerUiAction.InitialPlayer)
                    onAction(PlayerUiAction.StartPlayerBar)
                }
                Lifecycle.Event.ON_STOP -> {
                    onAction(PlayerUiAction.EndPlayerBar)
                }
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            onAction(PlayerUiAction.EndPlayerBar)
            playerViewRef.value?.player = null
            playerViewRef.value = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding(),
    ) {
        // 1. 播放器 Player (16:9)
        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            factory = { context ->
                PlayerView(context).apply {
                    useController = true
                    keepScreenOn = true
                    setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                }
            },
            update = { playerView ->
                playerViewRef.value = playerView
                if (playerView.player != controller) {
                    playerView.player = controller
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. 進度條與時間
        PlayerSlider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            currentMills = state.currentMills,
            totalMills = state.totalMills,
            onSeekTo = { onAction(PlayerUiAction.SeekTo(positionMs = it)) },
        )

        Spacer(modifier = Modifier.height(30.dp))

        // 3. 播放器按控制按鈕組
        PlayerAction(
            isPlaying = state.isPlaying,
            onPrevClick = { onAction(PlayerUiAction.SkipPrev1s) },
            onPrev10Click = { onAction(PlayerUiAction.SkipPrev10s) },
            onPlayPauseClick = { onAction(PlayerUiAction.TogglePlayPause) },
            onNext10Click = { onAction(PlayerUiAction.SkipNext10s) },
            onNextClick = { onAction(PlayerUiAction.SkipNext1s) },
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 4. 媒體資訊卡片
        MediaInfo(data = state.data)
    }
}

@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light Mode"
)
@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
fun PlayerScreenPreview() {
    AlexHLSExoPlayerV2ComposeTheme {
        PlayerScreen(
            state = PlayerUiState(
                isPlaying = false,
                currentMills = 65_000L,
                totalMills = 300_000L,
                data = PlaybackNotificationData()
            ),
            controller = null
        )
    }
}