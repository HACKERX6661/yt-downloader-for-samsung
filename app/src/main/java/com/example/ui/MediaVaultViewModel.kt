package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.MediaItemEntity
import com.example.data.MediaRepository
import com.example.data.MediaStoreHelper
import com.example.downloader.DownloadEngine
import com.example.downloader.DownloadRequest
import com.example.downloader.DownloadStatus
import com.example.downloader.DownloadTaskProgress
import com.example.downloader.MediaMetadataInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab {
    DOWNLOADER,
    LIBRARY
}

enum class LibraryFilter {
    ALL,
    VIDEOS,
    AUDIO
}

class MediaVaultViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MediaRepository
    val downloadEngine: DownloadEngine

    init {
        val db = AppDatabase.getInstance(application)
        repository = MediaRepository(application, db.mediaDao())
        downloadEngine = DownloadEngine(application, repository)
    }

    // Navigation & Tabs
    private val _currentTab = MutableStateFlow(MainTab.DOWNLOADER)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    fun setTab(tab: MainTab) {
        _currentTab.value = tab
    }

    // URL & Metadata Fetching
    private val _inputUrl = MutableStateFlow("")
    val inputUrl: StateFlow<String> = _inputUrl.asStateFlow()

    private val _isFetching = MutableStateFlow(false)
    val isFetching: StateFlow<Boolean> = _isFetching.asStateFlow()

    private val _fetchedMetadata = MutableStateFlow<MediaMetadataInfo?>(null)
    val fetchedMetadata: StateFlow<MediaMetadataInfo?> = _fetchedMetadata.asStateFlow()

    private val _fetchError = MutableStateFlow<String?>(null)
    val fetchError: StateFlow<String?> = _fetchError.asStateFlow()

    // Step-by-Step Selection Workflow
    private val _isAudioOnly = MutableStateFlow(false)
    val isAudioOnly: StateFlow<Boolean> = _isAudioOnly.asStateFlow()

    private val _selectedVideoFormat = MutableStateFlow("MP4")
    val selectedVideoFormat: StateFlow<String> = _selectedVideoFormat.asStateFlow()

    private val _selectedAudioFormat = MutableStateFlow("MP3")
    val selectedAudioFormat: StateFlow<String> = _selectedAudioFormat.asStateFlow()

    private val _selectedResolution = MutableStateFlow("1080p Full HD")
    val selectedResolution: StateFlow<String> = _selectedResolution.asStateFlow()

    private val _selectedBitrate = MutableStateFlow("Ultra High (320kbps / Original)")
    val selectedBitrate: StateFlow<String> = _selectedBitrate.asStateFlow()

    // Real-Time Downloads
    val currentDownloadTask: StateFlow<DownloadTaskProgress?> = downloadEngine.currentTask
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Dialogs & Safety
    private val _showPermissionDialog = MutableStateFlow(false)
    val showPermissionDialog: StateFlow<Boolean> = _showPermissionDialog.asStateFlow()

    private val prefs = application.getSharedPreferences("media_vault_prefs", Context.MODE_PRIVATE)
    private val _showLegalDisclaimer = MutableStateFlow(!prefs.getBoolean("disclaimer_accepted", false))
    val showLegalDisclaimer: StateFlow<Boolean> = _showLegalDisclaimer.asStateFlow()

    // Library Filtering & Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _libraryFilter = MutableStateFlow(LibraryFilter.ALL)
    val libraryFilter: StateFlow<LibraryFilter> = _libraryFilter.asStateFlow()

    val libraryMedia: StateFlow<List<MediaItemEntity>> = combine(
        repository.allMedia,
        _searchQuery,
        _libraryFilter
    ) { allItems, query, filter ->
        allItems.filter { item ->
            val matchesQuery = query.isBlank() || item.title.contains(query, ignoreCase = true)
            val matchesFilter = when (filter) {
                LibraryFilter.ALL -> true
                LibraryFilter.VIDEOS -> !item.isAudioOnly
                LibraryFilter.AUDIO -> item.isAudioOnly
            }
            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Options Sheet & Active Player
    private val _optionsMediaItem = MutableStateFlow<MediaItemEntity?>(null)
    val optionsMediaItem: StateFlow<MediaItemEntity?> = _optionsMediaItem.asStateFlow()

    private val _activePlayingMedia = MutableStateFlow<MediaItemEntity?>(null)
    val activePlayingMedia: StateFlow<MediaItemEntity?> = _activePlayingMedia.asStateFlow()

    // Presets
    val presetMedia = downloadEngine.presetMedia

    // Actions
    fun onUrlChanged(newUrl: String) {
        _inputUrl.value = newUrl
        _fetchError.value = null
    }

    fun selectPreset(preset: MediaMetadataInfo) {
        _inputUrl.value = preset.url
        _fetchedMetadata.value = preset
        _fetchError.value = null
    }

    fun fetchMediaDetails() {
        val url = _inputUrl.value.trim()
        if (url.isBlank()) {
            _fetchError.value = "Please enter or paste a valid media URL"
            return
        }

        viewModelScope.launch {
            _isFetching.value = true
            _fetchError.value = null
            val result = downloadEngine.extractMetadata(url)
            result.onSuccess { metadata ->
                _fetchedMetadata.value = metadata
                _fetchError.value = null
            }.onFailure { err ->
                _fetchError.value = err.localizedMessage ?: "Failed to extract media details"
            }
            _isFetching.value = false
        }
    }

    fun setIsAudioOnly(value: Boolean) {
        _isAudioOnly.value = value
    }

    fun setSelectedVideoFormat(format: String) {
        _selectedVideoFormat.value = format
    }

    fun setSelectedAudioFormat(format: String) {
        _selectedAudioFormat.value = format
    }

    fun setSelectedResolution(resolution: String) {
        _selectedResolution.value = resolution
    }

    fun setSelectedBitrate(bitrate: String) {
        _selectedBitrate.value = bitrate
    }

    fun requestDownload() {
        val meta = _fetchedMetadata.value ?: return
        // Trigger storage permission check if needed
        _showPermissionDialog.value = true
    }

    fun onPermissionGrantedAndStartDownload() {
        _showPermissionDialog.value = false
        val meta = _fetchedMetadata.value ?: return

        val request = DownloadRequest(
            metadata = meta,
            isAudioOnly = _isAudioOnly.value,
            selectedVideoFormat = _selectedVideoFormat.value,
            selectedAudioFormat = _selectedAudioFormat.value,
            selectedResolution = _selectedResolution.value,
            selectedBitrate = _selectedBitrate.value
        )

        viewModelScope.launch {
            downloadEngine.executeDownload(request)
        }
    }

    fun dismissPermissionDialog() {
        _showPermissionDialog.value = false
    }

    fun acceptLegalDisclaimer() {
        prefs.edit().putBoolean("disclaimer_accepted", true).apply()
        _showLegalDisclaimer.value = false
    }

    fun openLegalDisclaimer() {
        _showLegalDisclaimer.value = true
    }

    fun dismissLegalDisclaimer() {
        _showLegalDisclaimer.value = false
    }

    fun dismissDownloadTask() {
        downloadEngine.dismissCurrentTask()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun setLibraryFilter(filter: LibraryFilter) {
        _libraryFilter.value = filter
    }

    fun openMediaOptions(item: MediaItemEntity) {
        _optionsMediaItem.value = item
    }

    fun closeMediaOptions() {
        _optionsMediaItem.value = null
    }

    fun playMediaInApp(item: MediaItemEntity) {
        _activePlayingMedia.value = item
        _optionsMediaItem.value = null
    }

    fun playMediaByUri(uriString: String) {
        viewModelScope.launch {
            val list = libraryMedia.value
            val match = list.find { it.localUri == uriString }
            if (match != null) {
                _activePlayingMedia.value = match
            } else {
                // Create temporary media item for playback
                _activePlayingMedia.value = MediaItemEntity(
                    title = currentDownloadTask.value?.title ?: "Saved Media",
                    sourceUrl = "",
                    localUri = uriString,
                    isAudioOnly = currentDownloadTask.value?.isAudioOnly ?: false
                )
            }
        }
    }

    fun closePlayer() {
        _activePlayingMedia.value = null
    }

    fun openInGallery(item: MediaItemEntity) {
        MediaStoreHelper.openInGallery(getApplication(), item.localUri, item.isAudioOnly)
        _optionsMediaItem.value = null
    }

    fun shareMedia(item: MediaItemEntity) {
        MediaStoreHelper.shareMedia(getApplication(), item.localUri, item.title, item.isAudioOnly)
        _optionsMediaItem.value = null
    }

    fun deleteMedia(item: MediaItemEntity) {
        viewModelScope.launch {
            repository.delete(item)
            if (_activePlayingMedia.value?.id == item.id) {
                _activePlayingMedia.value = null
            }
            _optionsMediaItem.value = null
        }
    }
}
