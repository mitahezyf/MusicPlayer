package com.example.musicplayer

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class MusicPlayer(private val context: Context) {
    private var exoPlayer: ExoPlayer? = null

    fun initializePlayer() {
        exoPlayer = ExoPlayer.Builder(context).build()
    }

    fun playMusic(filePath: String) {
        exoPlayer?.apply {
            setMediaItem(MediaItem.fromUri(filePath))
            prepare()
            play()
        }
    }

    fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun resume() {
        exoPlayer?.play()
    }

    fun getProgress(): Float {
        return exoPlayer?.let {
            if (it.duration > 0) it.currentPosition.toFloat() / it.duration else 0f
        } ?: 0f
    }

    fun setLooping(isLooping: Boolean) {
        exoPlayer?.repeatMode = if (isLooping) ExoPlayer.REPEAT_MODE_ONE else ExoPlayer.REPEAT_MODE_OFF
    }
}
