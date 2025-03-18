package com.example.musicplayer

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

data class MusicFile(val fileName: String, val filePath: String)

class MusicScanner(private val context: Context) {
    fun getMusicFiles(): List<MusicFile> {
        val musicList = mutableListOf<MusicFile>()
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA)

        val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val pathColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while (it.moveToNext()) {
                val name = it.getString(nameColumn)
                val path = it.getString(pathColumn)
                musicList.add(MusicFile(name, path))
            }
        }

        return musicList
    }
}
