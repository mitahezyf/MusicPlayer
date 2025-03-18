package com.example.musicplayer

import android.app.Application
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private val musicScanner = MusicScanner(context)
    private val musicPlayer = MusicPlayer(context)

    private val _musicFiles = MutableStateFlow<List<MusicFile>>(emptyList())
    val musicFiles = _musicFiles.asStateFlow()

    private val _currentIndex = MutableStateFlow(-1)
    val currentIndex = _currentIndex.asStateFlow()

    private val _currentSong = MutableStateFlow<String?>(null)
    val currentSong = _currentSong.asStateFlow()

    private val _currentAlbumArt = MutableStateFlow<Bitmap?>(null)
    val currentAlbumArt = _currentAlbumArt.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress = _progress.asStateFlow()

    private val _isLooping = MutableStateFlow(false)
    val isLooping = _isLooping.asStateFlow()

    private val _isShuffling = MutableStateFlow(false)
    val isShuffling = _isShuffling.asStateFlow()

    private val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO)
    } else {
        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private val _hasPermission = MutableStateFlow(checkPermissions())
    val hasPermission = _hasPermission.asStateFlow()

    init {
        if (_hasPermission.value) {
            loadMusicFiles()
        }
        musicPlayer.initializePlayer()
        startProgressUpdater()
    }

    private fun checkPermissions(): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun loadMusicFiles() {
        viewModelScope.launch {
            val files = musicScanner.getMusicFiles()
            _musicFiles.value = files
            if (files.isNotEmpty()) {
                    // Odtwarza pierwszy utwór po załadowaniu plików
                playMusicAtIndex(0)
            }
        }
    }

    fun playMusic(filePath: String) {
        _currentSong.value = filePath
        musicPlayer.playMusic(filePath)
        _isPlaying.value = true
    }

    fun playMusicAtIndex(index: Int) {
        if (index in _musicFiles.value.indices) {
            _currentIndex.value = index
            val file = _musicFiles.value[index]
            _currentSong.value = file.fileName
                // Pobranie okładki
            _currentAlbumArt.value = musicScanner.getAlbumArt(file.filePath)
            musicPlayer.playMusic(file.filePath)
            _isPlaying.value = true
        }
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            musicPlayer.pause()
        } else {
            musicPlayer.resume()
        }
        _isPlaying.value = !_isPlaying.value
    }

    private fun startProgressUpdater() {
        viewModelScope.launch {
            while (true) {
                _progress.value = musicPlayer.getProgress()
                delay(1000) // Aktualizacja co 1s
            }
        }
    }

    fun skipToNext() {
        val nextIndex = if (_isShuffling.value) {
            (0 until _musicFiles.value.size).random()
        } else {
            (_currentIndex.value + 1) % _musicFiles.value.size
        }
        playMusicAtIndex(nextIndex)
    }

    fun skipToPrevious() {
        if (musicPlayer.getProgress() < 0.05f) {
            val prevIndex = if (_currentIndex.value > 0) _currentIndex.value - 1 else _musicFiles.value.lastIndex
            playMusicAtIndex(prevIndex)
        } else {
            musicPlayer.playMusic(_musicFiles.value[_currentIndex.value].filePath)
        }
    }

    fun toggleLooping() {
        _isLooping.value = !_isLooping.value
        musicPlayer.setLooping(_isLooping.value)
    }

    fun toggleShuffle() {
        _isShuffling.value = !_isShuffling.value
    }

    override fun onCleared() {
        super.onCleared()
        musicPlayer.releasePlayer()
    }
}
