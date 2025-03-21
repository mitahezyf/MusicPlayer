package com.example.musicplayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun MiniPlayerContainer(
    viewModel: MusicViewModel,
    onExpand: () -> Unit
) {
    val currentSongTitle by viewModel.currentSongTitle.collectAsState()
    val currentArtist by viewModel.currentArtist.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val progress by viewModel.progress.collectAsState()

    MiniPlayerUI(
        currentSongTitle = currentSongTitle,
        currentArtist = currentArtist,
        isPlaying = isPlaying,
        progress = progress,
        onPlayPause = { viewModel.togglePlayPause() },
        onSkipNext = { viewModel.skipToNext() },
        onSkipPrevious = { viewModel.skipToPrevious() },
        onExpand = onExpand
    )
}