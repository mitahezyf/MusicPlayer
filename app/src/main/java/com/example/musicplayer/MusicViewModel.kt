package com.example.musicplayer

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    private val musicScanner = MusicScanner(context)
    private val musicPlayer = MusicPlayer(context)
    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("music_prefs", Context.MODE_PRIVATE)

    private val _musicFiles = MutableStateFlow<List<MusicFile>>(emptyList())
    val musicFiles = _musicFiles.asStateFlow()

    private val _currentIndex = MutableStateFlow(-1)
//    val currentIndex = _currentIndex.asStateFlow()

    private val _currentAlbumArt = MutableStateFlow<Bitmap?>(null)
    val currentAlbumArt = _currentAlbumArt.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress = _progress.asStateFlow()

//    private val _isLooping = MutableStateFlow(false)
//    val isLooping = _isLooping.asStateFlow()

    private val _isShuffling = MutableStateFlow(false)
    val isShuffling = _isShuffling.asStateFlow()

    private val _timerDuration = MutableStateFlow(0L)
//    val timerDuration = _timerDuration.asStateFlow()

    private val _timerActive = MutableStateFlow(false)
    val timerActive = _timerActive.asStateFlow()

    private val _currentSongTitle = MutableStateFlow("Unknown Title")
    val currentSongTitle = _currentSongTitle.asStateFlow()

    private val _currentArtist = MutableStateFlow("Unknown Artist")
    val currentArtist = _currentArtist.asStateFlow()

    private val _currentDuration = MutableStateFlow(0L)
    val currentDuration = _currentDuration.asStateFlow()

    init {
        loadMusicFiles()
        musicPlayer.initializePlayer()
        startProgressUpdater()
        musicPlayer.setOnCompletionListener { skipToNext() }
    }

//    private fun checkPermissions(): Boolean {
//        return arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO).all {
//            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
//        }
//    }

    fun loadMusicFiles() {
        viewModelScope.launch {
            val files = musicScanner.getMusicFiles()
            _musicFiles.value = files
            if (files.isNotEmpty()) restoreLastPlayedSong() else resetUIState()
        }
    }

    private fun resetUIState() {
        _currentIndex.value = -1
        _currentSongTitle.value = "Unknown Title"
        _currentArtist.value = "Unknown Artist"
        _currentDuration.value = 0
        _currentAlbumArt.value = null
    }

    fun playMusic(filePath: String) {
        _musicFiles.value.find { it.filePath == filePath }?.let { file ->
            updateCurrentSongInfo(file)
            musicPlayer.prepare(filePath) {
                musicPlayer.play()
                _isPlaying.value = true
                saveLastPlayedSong()
            }
        }
    }

    private fun updateCurrentSongInfo(file: MusicFile) {
        _currentIndex.value = _musicFiles.value.indexOf(file)
        _currentSongTitle.value = file.title
        _currentArtist.value = file.artist ?: "Unknown Artist"
        _currentDuration.value = file.duration
        _currentAlbumArt.value = musicScanner.getAlbumArt(file.filePath)
    }

    private fun startProgressUpdater() {
        viewModelScope.launch {
            while (true) {
                val duration = musicPlayer.getDuration()
                val position = musicPlayer.getCurrentPosition()
                if (duration > 0 && position >= 0) {
                    _progress.value = position.toFloat() / duration
                    _currentDuration.value = duration
                }
                delay(500)
            }
        }
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            musicPlayer.pause()
            saveLastPlayedSong()
        } else {
            musicPlayer.play()
        }
        _isPlaying.value = !_isPlaying.value
    }

    fun skipToNext() {
        if (_musicFiles.value.isEmpty()) return

        val nextIndex = when {
            _isShuffling.value -> (0 until _musicFiles.value.size).random()
            else -> (_currentIndex.value + 1).let {
                if (it >= _musicFiles.value.size) 0 else it
            }
        }
        playMusicAtIndex(nextIndex)
    }

    fun skipToPrevious() {
        if (musicPlayer.getCurrentPosition() < 3000) {
            val prevIndex = if (_currentIndex.value > 0) {
                _currentIndex.value - 1
            } else {
                _musicFiles.value.lastIndex
            }
            playMusicAtIndex(prevIndex)
        } else {
            musicPlayer.seekTo(0)
        }
    }

    private fun playMusicAtIndex(index: Int) {
        if (index in _musicFiles.value.indices) {
            val file = _musicFiles.value[index]
            updateCurrentSongInfo(file)
            musicPlayer.prepare(file.filePath) {
                musicPlayer.play()
                _isPlaying.value = true
                saveLastPlayedSong()
            }
        }
    }

//    fun toggleLooping() {
//        _isLooping.value = !_isLooping.value
//        musicPlayer.setLooping(_isLooping.value)
//    }

    override fun onCleared() {
        saveLastPlayedSong()
        musicPlayer.releasePlayer()
        super.onCleared()
    }

//    fun seekTo(progress: Float) {
//        val duration = musicPlayer.getDuration()
//        if (duration > 0) {
//            val newPosition = (progress * duration).toLong()
//            musicPlayer.seekTo(newPosition)
//        }
//    }

    fun setTimer(durationInSeconds: Long) {
        _timerDuration.value = durationInSeconds
        _timerActive.value = true
        startTimer()
    }

    private fun startTimer() {
        viewModelScope.launch {
            delay(_timerDuration.value * 1000)
            if (_timerActive.value) {
                musicPlayer.pause()
                _isPlaying.value = false
                _timerActive.value = false
            }
        }
    }

    fun toggleShuffle() {
        _isShuffling.value = !_isShuffling.value
    }

    fun seekTo(progress: Float) {
        val duration = musicPlayer.getDuration()
        if (duration > 0) {
            val newPosition = (progress * duration).toLong()
            musicPlayer.seekTo(newPosition)
        }
    }

    private fun saveLastPlayedSong() {
        _currentIndex.value.takeIf { it != -1 }?.let { index ->
            sharedPreferences.edit().apply {
                putString("last_song", _musicFiles.value[index].filePath)
                putLong("last_position", musicPlayer.getCurrentPosition())
                apply()
            }
        }
    }

    private fun restoreLastPlayedSong() {
        val lastSongPath = sharedPreferences.getString("last_song", null)
        val lastPosition = sharedPreferences.getLong("last_position", 0L)

        lastSongPath?.let { path ->
            _musicFiles.value.indexOfFirst { it.filePath == path }.takeIf { it != -1 }?.let { index ->
                _currentIndex.value = index
                val file = _musicFiles.value[index]
                updateCurrentSongInfo(file)

                musicPlayer.prepare(path) {
                    musicPlayer.seekTo(lastPosition)
                    musicPlayer.pause()
                    _isPlaying.value = false
                }
            }
        }
    }

}