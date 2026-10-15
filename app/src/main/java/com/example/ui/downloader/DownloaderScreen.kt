package com.example.ui.downloader

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.downloader.DownloadStatus
import com.example.ui.MediaVaultViewModel
import com.example.ui.components.CyberpunkChip
import com.example.ui.components.CyberpunkGlassCard
import com.example.ui.components.CyberpunkGradientButton
import com.example.ui.components.CyberpunkIconButton
import com.example.ui.components.RealTimeDownloadCard
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.GlowingEmerald
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TranslucentCharcoal
import com.example.ui.theme.TranslucentCharcoalElevated

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DownloaderScreen(
    viewModel: MediaVaultViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val inputUrl by viewModel.inputUrl.collectAsStateWithLifecycle()
    val isFetching by viewModel.isFetching.collectAsStateWithLifecycle()
    val fetchedMetadata by viewModel.fetchedMetadata.collectAsStateWithLifecycle()
    val fetchError by viewModel.fetchError.collectAsStateWithLifecycle()

    val isAudioOnly by viewModel.isAudioOnly.collectAsStateWithLifecycle()
    val selectedVideoFormat by viewModel.selectedVideoFormat.collectAsStateWithLifecycle()
    val selectedAudioFormat by viewModel.selectedAudioFormat.collectAsStateWithLifecycle()
    val selectedResolution by viewModel.selectedResolution.collectAsStateWithLifecycle()
    val selectedBitrate by viewModel.selectedBitrate.collectAsStateWithLifecycle()
    val currentDownloadTask by viewModel.currentDownloadTask.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("downloader_screen")
    ) {
        // App Header Banner
        CyberpunkGlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp,
            borderGlowColor = NeonCyan.copy(alpha = 0.3f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Glowing Logo Symbol
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.15f))
                        .border(1.5.dp, NeonCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "MediaVault",
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "MEDIAVAULT",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(GlowingEmerald.copy(alpha = 0.2f))
                                .border(1.dp, GlowingEmerald.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "PRO",
                                color = GlowingEmerald,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "yt-dlp & FFmpeg Stream Engine • MediaStore Sync",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                CyberpunkIconButton(
                    icon = Icons.Default.Security,
                    onClick = { viewModel.openLegalDisclaimer() },
                    contentDescription = "Safety & Legal Info",
                    tint = ElectricPurple,
                    size = 40.dp,
                    testTag = "legal_info_btn"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preset Quick Samples
        Column {
            Text(
                text = "QUICK TEST PRESETS",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.presetMedia.forEach { preset ->
                    val isSelected = inputUrl == preset.url
                    CyberpunkChip(
                        text = preset.title.take(20) + "…",
                        selected = isSelected,
                        badgeText = if (preset.durationSeconds > 300) "HD" else "AUDIO",
                        onClick = { viewModel.selectPreset(preset) },
                        testTag = "preset_${preset.title.take(6).lowercase()}"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Link Input Section
        CyberpunkGlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "PASTE MEDIA LINK",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = inputUrl,
                    onValueChange = { viewModel.onUrlChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("url_input_field"),
                    placeholder = {
                        Text(
                            text = "https://youtube.com/watch?v=... or direct video URL",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = NeonCyan
                        )
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (inputUrl.isNotBlank()) {
                                IconButton(
                                    onClick = { viewModel.onUrlChanged("") },
                                    modifier = Modifier.testTag("clear_url_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = TextMuted
                                    )
                                }
                            }
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                    val clip = clipboard?.primaryClip?.getItemAt(0)?.text?.toString()
                                    if (!clip.isNullOrBlank()) {
                                        viewModel.onUrlChanged(clip)
                                    }
                                },
                                modifier = Modifier.testTag("paste_clipboard_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentPaste,
                                    contentDescription = "Paste from Clipboard",
                                    tint = NeonCyan
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color(0x33FFFFFF),
                        focusedContainerColor = TranslucentCharcoalElevated,
                        unfocusedContainerColor = TranslucentCharcoalElevated,
                        cursorColor = NeonCyan,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                if (fetchError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = fetchError ?: "",
                        color = NeonPink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                CyberpunkGradientButton(
                    text = "Fetch Media Details",
                    onClick = { viewModel.fetchMediaDetails() },
                    isLoading = isFetching,
                    enabled = inputUrl.isNotBlank(),
                    testTag = "fetch_details_btn"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Fetched Media Preview Card & Step-by-Step Workflow
        AnimatedVisibility(
            visible = fetchedMetadata != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            fetchedMetadata?.let { meta ->
                Column {
                    // Preview Card
                    CyberpunkGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 20.dp,
                        borderGlowColor = NeonCyan.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Thumbnail with badges
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(TranslucentCharcoalElevated)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(meta.thumbnailUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = meta.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Duration Badge
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MidnightNavy.copy(alpha = 0.85f))
                                        .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    val mins = meta.durationSeconds / 60
                                    val secs = meta.durationSeconds % 60
                                    Text(
                                        text = String.format("%d:%02d", mins, secs),
                                        color = TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // 4K/HD Tag
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(NeonCyan.copy(alpha = 0.85f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "STREAM READY",
                                        color = MidnightNavy,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = meta.title,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = meta.author,
                                    color = ElectricPurple,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    text = "Est. ${(meta.approxSizeBytes / (1024 * 1024))} MB",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Step-by-Step Selection Workflow
                    CyberpunkGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 20.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "DOWNLOAD WORKFLOW CONFIGURATION",
                                color = NeonCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // 1. Mode Selection
                            Text(
                                text = "1. Select Mode",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CyberpunkChip(
                                    text = "Video + Audio",
                                    selected = !isAudioOnly,
                                    onClick = { viewModel.setIsAudioOnly(false) },
                                    modifier = Modifier.weight(1f),
                                    badgeText = "FULL",
                                    testTag = "mode_video_btn"
                                )
                                CyberpunkChip(
                                    text = "Audio Only",
                                    selected = isAudioOnly,
                                    onClick = { viewModel.setIsAudioOnly(true) },
                                    modifier = Modifier.weight(1f),
                                    badgeText = "HQ",
                                    testTag = "mode_audio_btn"
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 2. Video Format (if video mode selected)
                            if (!isAudioOnly) {
                                Text(
                                    text = "2. Video Container Format",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    meta.availableVideoFormats.forEach { fmt ->
                                        CyberpunkChip(
                                            text = fmt,
                                            selected = selectedVideoFormat == fmt,
                                            onClick = { viewModel.setSelectedVideoFormat(fmt) },
                                            modifier = Modifier.weight(1f),
                                            testTag = "video_format_$fmt"
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // 3. Video Resolution
                                Text(
                                    text = "3. Video Resolution",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    meta.availableResolutions.forEach { res ->
                                        CyberpunkChip(
                                            text = res,
                                            selected = selectedResolution == res,
                                            onClick = { viewModel.setSelectedResolution(res) },
                                            badgeText = if (res.contains("4K") || res.contains("2K")) "UHD" else null,
                                            testTag = "resolution_${res.take(4).trim()}"
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                            }

                            // 4. Audio Format
                            Text(
                                text = if (isAudioOnly) "2. Audio Codec & Format" else "4. Audio Track Format",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                meta.availableAudioFormats.forEach { fmt ->
                                    CyberpunkChip(
                                        text = fmt,
                                        selected = selectedAudioFormat == fmt,
                                        onClick = { viewModel.setSelectedAudioFormat(fmt) },
                                        badgeText = if (fmt == "FLAC") "LOSSLESS" else null,
                                        testTag = "audio_format_$fmt"
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 5. Bitrate / Quality
                            Text(
                                text = if (isAudioOnly) "3. Audio Bitrate Quality" else "5. Stream Quality & Bitrate",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                meta.availableBitrates.forEach { bitrate ->
                                    CyberpunkChip(
                                        text = bitrate,
                                        selected = selectedBitrate == bitrate,
                                        onClick = { viewModel.setSelectedBitrate(bitrate) },
                                        modifier = Modifier.fillMaxWidth(),
                                        testTag = "bitrate_${bitrate.take(5)}"
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Action Button: Download to Gallery & Library
                            CyberpunkGradientButton(
                                text = "Download to Gallery & Library",
                                onClick = { viewModel.requestDownload() },
                                icon = Icons.Default.Download,
                                isEmerald = true,
                                enabled = currentDownloadTask == null || currentDownloadTask?.status == DownloadStatus.COMPLETED || currentDownloadTask?.status == DownloadStatus.ERROR,
                                testTag = "download_to_gallery_btn"
                            )
                        }
                    }
                }
            }
        }

        // Real-Time Download Card
        AnimatedVisibility(
            visible = currentDownloadTask != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            currentDownloadTask?.let { task ->
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    RealTimeDownloadCard(
                        task = task,
                        onDismiss = { viewModel.dismissDownloadTask() },
                        onPlay = { uri -> viewModel.playMediaByUri(uri) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
