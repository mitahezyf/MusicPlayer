package com.example.musicplayer

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.Locale


@Composable
fun PlayerScreen(viewModel: MusicViewModel, navController: NavController) {

    val context = LocalContext.current

    val isShuffling by viewModel.isShuffling.collectAsState()
    val timerActive by viewModel.timerActive.collectAsState()

    val progress by viewModel.progress.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    val currentSongTitle by viewModel.currentSongTitle.collectAsState()
    val currentArtist by viewModel.currentArtist.collectAsState()
    val albumArt by viewModel.currentAlbumArt.collectAsState()
    val currentDuration by viewModel.currentDuration.collectAsState()

    fun formatTime(milliseconds: Long): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        return String.format(Locale.US,"%02d:%02d", minutes, seconds)
    }

    val currentTime = formatTime((progress * currentDuration).toLong())
    val totalTime = formatTime(currentDuration)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(20.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp, start = 15.dp, end = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Minimalizacja
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .size(40.dp)
//                    .background(Color.Red)
            ) {
                Icon(
                    imageVector = Icons.Default.ExpandMore, // Strzałka w dół
                    contentDescription = "Minimize Player",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            //Opcje
            IconButton(
                onClick = {
                    Toast.makeText(
                        context,
                        "co gagatku, spodziewałeś się że zadziała?",
                        Toast.LENGTH_SHORT //
                    ).show()
                },
                modifier = Modifier
                    .size(40.dp)
//                    .background(Color.Red)

            ) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = "Opcje",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        //Okładka albumu
        albumArt?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "Album Art",
            modifier = Modifier
                .size(450.dp)
                .aspectRatio(1f)
                .padding(16.dp)
        )
        } ?: Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = "Default Album Art",
            modifier = Modifier.size(250.dp)
        )

        //Tytuł i wykonawca
        Text(text = currentSongTitle, fontSize = 22.sp, color = Color.White)
        Text(text = currentArtist, fontSize = 16.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(20.dp))

        //Pasek postępu

        Slider(
            value = progress,
            onValueChange = { newProgress ->

                viewModel.seekTo(newProgress)
            },
//            onValueChangeFinished = {/*todo cos dodatkowego na koniec przesowania*/ }

            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = Color.Red,
                activeTrackColor = Color.Red
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = currentTime, color = Color.Gray)
            Text(text = totalTime, color = Color.Gray)
        }

        //Przyciski sterowania
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //Mieszanie
            IconButton(
                onClick = { viewModel.toggleShuffle() },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = if (isShuffling) Icons.Default.ShuffleOn else Icons.Default.Shuffle,
                    contentDescription = "Mieszaj",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            //PRZEWIJANIE WSTECZ
            IconButton(
                onClick = { viewModel.skipToPrevious() },
                modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.SkipPrevious,
                    contentDescription = "Poprzedni utwór",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }

            //PLAY / PAUSE
            IconButton(
                onClick = { viewModel.togglePlayPause() },
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = Color.Black,
                    modifier = Modifier.size(40.dp)
                )
            }

            //PRZEWIJANIE DO PRZODU
            IconButton(
                onClick = { viewModel.skipToNext() },
                modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Następny utwór",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }
            //Wyłącznik czasowy
            IconButton(
                onClick = { viewModel.setTimer(300) },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = "Timer",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Timer Active Status
        if (timerActive) {
            Text(
                text = "Wyłącznik czasowy ustawiony(3 minuty)",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(16.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

