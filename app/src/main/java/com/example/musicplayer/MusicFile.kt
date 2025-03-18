package com.example.musicplayer

data class MusicFile(
    val id: Long,
    val filePath: String,
    val fileName: String,
    val album: String?,
    val artist: String?
)
