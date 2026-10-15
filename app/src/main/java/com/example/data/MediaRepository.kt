package com.example.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class MediaRepository(
    private val context: Context,
    private val mediaDao: MediaDao
) {
    val allMedia: Flow<List<MediaItemEntity>> = mediaDao.getAllMedia()

    fun getMediaByType(audioOnly: Boolean): Flow<List<MediaItemEntity>> =
        mediaDao.getMediaByType(audioOnly)

    fun searchMedia(query: String): Flow<List<MediaItemEntity>> =
        mediaDao.searchMedia(query)

    suspend fun insert(item: MediaItemEntity): Long = withContext(Dispatchers.IO) {
        mediaDao.insertMedia(item)
    }

    suspend fun delete(item: MediaItemEntity) = withContext(Dispatchers.IO) {
        MediaStoreHelper.deleteMediaUri(context, item.localUri)
        mediaDao.deleteMedia(item)
    }

    suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        val item = mediaDao.getMediaById(id)
        if (item != null) {
            delete(item)
        }
    }
}
