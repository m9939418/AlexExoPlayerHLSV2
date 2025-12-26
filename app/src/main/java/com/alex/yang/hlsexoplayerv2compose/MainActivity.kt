package com.alex.yang.hlsexoplayerv2compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alex.yang.hlsexoplayerv2compose.feature.player.presentation.PlayerScreen
import com.alex.yang.hlsexoplayerv2compose.feature.player.presentation.PlayerViewModel
import com.alex.yang.hlsexoplayerv2compose.ui.theme.AlexHLSExoPlayerV2ComposeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlexHLSExoPlayerV2ComposeTheme {
                val viewModel = hiltViewModel<PlayerViewModel>()
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                val controller by viewModel.controller.collectAsStateWithLifecycle()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        PlayerScreen(
                            modifier = Modifier.padding(innerPadding),
                            state = state,
                            controller = controller,
                            onAction = viewModel::onAction
                        )
                }
            }
        }
    }
}