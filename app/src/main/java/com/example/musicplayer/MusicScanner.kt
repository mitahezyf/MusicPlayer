package com.example.musicplayer

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever

class MusicScanner(private val context: Context) {

    fun getMusicFiles(): List<MusicFile> {
        val musicFiles = mutableListOf<MusicFile>()

        // Zapytanie MediaStore o pliki audio
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST
        )

        val cursor: Cursor? = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val data = it.getString(dataColumn)
                val name = it.getString(nameColumn)
                val album = it.getString(albumColumn)
                val artist = it.getString(artistColumn)

                // Utwórz obiekt MusicFile i dodaj go do listy.
                val musicFile = MusicFile(id, data, name, album, artist)
                musicFiles.add(musicFile)
            }
        }
        return musicFiles
    }

    fun getAlbumArt(filePath: String): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(filePath)
            val art = retriever.embeddedPicture
            if (art != null) {
                BitmapFactory.decodeByteArray(art, 0, art.size)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        } finally {
            retriever.release()
        }
    }
}
