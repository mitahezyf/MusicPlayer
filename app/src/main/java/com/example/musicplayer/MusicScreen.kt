@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.musicplayer

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavController

@Composable
fun MusicScreen(navController: NavController, viewModel: MusicViewModel) {
    val musicFiles by viewModel.musicFiles.collectAsState()
    val hasPermission by viewModel.hasPermission.collectAsState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            viewModel.loadMusicFiles()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            val neededPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
            } else {
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            permissionLauncher.launch(neededPermissions)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Music Player") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (hasPermission) {
                LazyColumn {
                    items(musicFiles) { musicFile ->
                        MusicItem(musicFile) { filePath ->
                            viewModel.playMusic(filePath)
                            navController.navigate("player")
                        }
                    }
                }
            } else {
                Text("Brak uprawnień do odczytu plików!", modifier = Modifier.padding(16.dp))
            }
        }
    }
}
