package com.example.musicplayer

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    private val _musicFiles = MutableStateFlow<List<MusicFile>>(emptyList())
    val musicFiles = _musicFiles.asStateFlow()

    private val context = getApplication<Application>().applicationContext
    private val musicScanner = MusicScanner(context)
    private val musicPlayer = MusicPlayer(context)

    private val _currentSong = MutableStateFlow<String?>(null)
    val currentSong = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress = _progress.asStateFlow()

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
    }

    private fun checkPermissions(): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestPermissions(callback: (Boolean) -> Unit) {
        callback(checkPermissions())
    }

    fun loadMusicFiles() {
        viewModelScope.launch {
            _musicFiles.value = musicScanner.getMusicFiles()
        }
    }

    fun playMusic(filePath: String) {
        _currentSong.value = filePath.substringAfterLast("/")
        musicPlayer.playMusic(filePath)
        _isPlaying.value = true
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            musicPlayer.pause()
        } else {
            musicPlayer.resume()
        }
        _isPlaying.value = !_isPlaying.value
    }

    fun updateProgress() {
        _progress.value = musicPlayer.getProgress()
    }

    override fun onCleared() {
        super.onCleared()
        musicPlayer.releasePlayer()
    }
}
