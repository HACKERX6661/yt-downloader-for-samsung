package com.example.downloader

enum class DownloadStatus {
    IDLE,
    CONNECTING,
    DOWNLOADING,
    MERGING,
    SAVED_TO_GALLERY,
    COMPLETED,
    ERROR
}

data class MediaMetadataInfo(
    val url: String,
    val title: String,
    val author: String,
    val thumbnailUrl: String,
    val durationSeconds: Long,
    val approxSizeBytes: Long,
    val availableResolutions: List<String> = listOf("4K (2160p)", "2K (1440p)", "1080p Full HD", "720p HD", "480p"),
    val availableAudioFormats: List<String> = listOf("MP3", "AAC", "M4A", "FLAC", "OPUS"),
    val availableVideoFormats: List<String> = listOf("MP4", "WEBM", "MKV"),
    val availableBitrates: List<String> = listOf("Ultra High (320kbps / Original)", "High (256kbps)", "Medium (128kbps)")
)

data class DownloadRequest(
    val metadata: MediaMetadataInfo,
    val isAudioOnly: Boolean,
    val selectedVideoFormat: String,
    val selectedAudioFormat: String,
    val selectedResolution: String,
    val selectedBitrate: String
)

data class DownloadTaskProgress(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val status: DownloadStatus = DownloadStatus.IDLE,
    val progressPercent: Float = 0f,
    val bytesDownloaded: Long = 0L,
    val totalBytes: Long = 0L,
    val downloadSpeedMBs: Float = 0f,
    val etaSeconds: Int = 0,
    val statusMessage: String = "",
    val savedUri: String? = null,
    val isAudioOnly: Boolean = false,
    val thumbnailUrl: String = "",
    val format: String = "",
    val resolution: String = ""
)
