

package com.example.musicplayer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.Image

@Composable
fun PlayerScreen(viewModel: MusicViewModel) {
    val currentSong by viewModel.currentSong.collectAsState()
    val albumArt by viewModel.currentAlbumArt.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        albumArt?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Album Art",
                modifier = Modifier
                    .size(250.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
        } ?: Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = "Default Album Art",
            modifier = Modifier.size(250.dp)
        )

        Text(
            text = currentSong ?: "Brak utworu",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        Slider(
            value = progress,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(16.dp)
        )

        Row {
            IconButton(onClick = { viewModel.skipToPrevious() }) {
                Icon(imageVector = Icons.Default.SkipPrevious, contentDescription = "Poprzedni")
            }

            IconButton(onClick = { viewModel.togglePlayPause() }) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Odtwarzanie"
                )
            }

            IconButton(onClick = { viewModel.skipToNext() }) {
                Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Następny")
            }
        }
    }
}
