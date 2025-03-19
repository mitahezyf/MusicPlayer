package com.example.musicplayer

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.musicplayer.PlayerScreen



@Composable
fun MusicNavigation(navController: androidx.navigation.NavHostController, viewModel: MusicViewModel) {
    NavHost(navController = navController, startDestination = "music_list") {
        composable("music_list") {
            MusicScreen(navController, viewModel)
        }
        composable("player") {
            PlayerScreen(viewModel)
        }
    }
}
