package com.example.musicplayer

data class MusicFile(
    val id: Long,
    val filePath: String,
    val title: String,
    val fileName: String,
    val album: String?,
    val artist: String?,
    //val collaborators: String?,
    val duration: Long

)

