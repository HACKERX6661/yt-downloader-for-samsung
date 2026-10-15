package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Query("SELECT * FROM downloaded_media ORDER BY downloadTimestamp DESC")
    fun getAllMedia(): Flow<List<MediaItemEntity>>

    @Query("SELECT * FROM downloaded_media WHERE isAudioOnly = :audioOnly ORDER BY downloadTimestamp DESC")
    fun getMediaByType(audioOnly: Boolean): Flow<List<MediaItemEntity>>

    @Query("SELECT * FROM downloaded_media WHERE title LIKE '%' || :query || '%' ORDER BY downloadTimestamp DESC")
    fun searchMedia(query: String): Flow<List<MediaItemEntity>>

    @Query("SELECT * FROM downloaded_media WHERE id = :id LIMIT 1")
    suspend fun getMediaById(id: Long): MediaItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(item: MediaItemEntity): Long

    @Query("DELETE FROM downloaded_media WHERE id = :id")
    suspend fun deleteMediaById(id: Long)

    @Delete
    suspend fun deleteMedia(item: MediaItemEntity)

    @Query("SELECT COUNT(*) FROM downloaded_media")
    fun getMediaCount(): Flow<Int>
}
