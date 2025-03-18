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
    //funkcje odtwarzacza
    fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }
    //pauza
    fun pause() {
        exoPlayer?.pause()
    }
    //wznowiene
    fun resume() {
        exoPlayer?.play()
    }
    //pasek postepu
    fun getProgress(): Float {
        return exoPlayer?.let {
            if (it.duration > 0) it.currentPosition.toFloat() / it.duration else 0f
        } ?: 0f
    }
    //pomin
    fun skipToNext() {
        exoPlayer?.apply {
            skipToNext()
        }
    }
    //przewin do tyłu
    fun skipToPrev () {
        exoPlayer?.apply {
            skipToPrev()
        }
    }
    //zapetlenie
    //wylacznik czasowsy
    //losowe
    //dodanie czasu
}
