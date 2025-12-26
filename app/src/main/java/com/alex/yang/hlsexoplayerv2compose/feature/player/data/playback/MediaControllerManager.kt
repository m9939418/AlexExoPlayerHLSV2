package com.alex.yang.hlsexoplayerv2compose.feature.player.data.playback

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.alex.yang.hlsexoplayerv2compose.feature.player.data.service.PlaybackService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by AlexYang on 2025/12/25.
 *
 *
 */
@Singleton
class MediaControllerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var controller: MediaController? = null
    private val mutex = Mutex()

    suspend fun get(): MediaController = mutex.withLock {
        controller?.takeIf { it.isConnected }?.let { return@withLock it }
        controller?.release()

        val token = SessionToken(
            context,
            ComponentName(context, PlaybackService::class.java)
        )

        controller = try {
            MediaController.Builder(context, token)
                .buildAsync()
                .await()
        } catch (e: Exception) {
            Log.e("DEBUG", "Failed to build controller", e)
            throw e
        }

        return requireNotNull(controller)
    }

    fun release() {
        controller?.release()
        controller = null
    }
}