package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_media")
data class MediaItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val sourceUrl: String,
    val localUri: String,
    val relativePath: String = "",
    val thumbnailUrl: String = "",
    val durationSeconds: Long = 0,
    val fileSizeBytes: Long = 0,
    val format: String = "MP4",
    val resolution: String = "1080p",
    val bitrate: String = "320kbps",
    val isAudioOnly: Boolean = false,
    val downloadTimestamp: Long = System.currentTimeMillis(),
    val mediaStoreId: Long = 0L
)
