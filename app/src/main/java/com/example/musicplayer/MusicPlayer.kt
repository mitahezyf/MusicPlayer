package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class MusicPlayer(private val context: Context) {
    private var exoPlayer: ExoPlayer? = null
    private var preparedListener: (() -> Unit)? = null

    private var isPrepared = false

    fun initializePlayer() {
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                @SuppressLint("SwitchIntDef")
                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_READY -> {
                            isPrepared = true
                            preparedListener?.invoke()
                        }
                        Player.STATE_ENDED -> isPrepared = false
                    }
                }
            })
        }
    }


    fun play() {
        if (isPrepared) {
            exoPlayer?.play()
        }
    }
    fun pause() {
        exoPlayer?.pause()
    }

    private var completionListener: Player.Listener? = null

    fun setOnCompletionListener(onCompletion: () -> Unit) {
        completionListener?.let { exoPlayer?.removeListener(it) }
        completionListener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    onCompletion()
                }
            }
        }
        exoPlayer?.addListener(completionListener!!)
    }

    fun prepare(filePath: String, onPrepared: () -> Unit) {
        exoPlayer?.let { player ->
            player.setMediaItem(MediaItem.fromUri(filePath))
            player.prepare()
            val listener = object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_READY) {
                        onPrepared()
                        player.removeListener(this)
                    }
                }
            }
            player.addListener(listener)
        }
    }

    fun getCurrentPosition(): Long = exoPlayer?.currentPosition ?: 0L
    fun getDuration(): Long = exoPlayer?.duration ?: 0L

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position.coerceAtLeast(0))
    }

//    fun playMusic(filePath: String) {
//        exoPlayer?.apply {
//            setMediaItem(MediaItem.fromUri(filePath))
//            prepare()
//            play()
//        }
//    }



//    fun isInitialized(): Boolean = exoPlayer != null
//    fun isPlaying(): Boolean = exoPlayer?.isPlaying ?: false

//    fun resume() {
//        exoPlayer?.play()
//    }

    fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

//    fun getProgress(): Float {
//        return exoPlayer?.let {
//            if (it.duration > 0) it.currentPosition.toFloat() / it.duration else 0f
//        } ?: 0f
//    }

//    fun setLooping(isLooping: Boolean) {
//        exoPlayer?.repeatMode = if (isLooping) {
//            ExoPlayer.REPEAT_MODE_ONE
//        } else {
//            ExoPlayer.REPEAT_MODE_OFF
//        }
//    }
}