package com.example.musicplayer

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever

class MusicScanner(private val context: Context) {

    // Funkcja zwracająca listę plików muzycznych
    fun getMusicFiles(): List<MusicFile> {
        val musicFiles = mutableListOf<MusicFile>()

        // Zapytanie MediaStore o pliki audio
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
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
            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val data = it.getString(dataColumn)
                val title = it.getString(titleColumn)
                val name = it.getString(nameColumn)
                val album = it.getString(albumColumn)
                val artist = it.getString(artistColumn)

                // Uzyskiwanie czasu trwania utworu
                val duration = getDuration(data)

                // Tworzenie obiektu MusicFile z przekazaniem duration
                val musicFile = MusicFile(id, data, title, name, album, artist, duration)
                musicFiles.add(musicFile)
            }
        }
        return musicFiles
    }

    // Funkcja pobierająca czas trwania utworu w milisekundach
    private fun getDuration(filePath: String): Long {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(filePath)
            val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            time?.toLong() ?: 0L // Zwraca czas trwania w milisekundach
        } catch (e: Exception) {
            0L
        } finally {
            retriever.release()
        }
    }

    // Funkcja pobierająca okładkę albumu z pliku muzycznego
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
