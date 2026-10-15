package com.example.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.MediaItemEntity
import com.example.ui.LibraryFilter
import com.example.ui.MainTab
import com.example.ui.MediaVaultViewModel
import com.example.ui.components.CyberpunkChip
import com.example.ui.components.CyberpunkGlassCard
import com.example.ui.components.CyberpunkGradientButton
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.GlowingEmerald
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TranslucentCharcoal
import com.example.ui.theme.TranslucentCharcoalElevated
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LibraryScreen(
    viewModel: MediaVaultViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val libraryFilter by viewModel.libraryFilter.collectAsStateWithLifecycle()
    val mediaList by viewModel.libraryMedia.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("library_screen")
    ) {
        // Search & Filter Header
        CyberpunkGlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("library_search_input"),
                    placeholder = {
                        Text("Search offline media...", color = TextMuted, fontSize = 13.sp)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = NeonCyan
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = TextMuted
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
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Filter Chips Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CyberpunkChip(
                        text = "All",
                        selected = libraryFilter == LibraryFilter.ALL,
                        onClick = { viewModel.setLibraryFilter(LibraryFilter.ALL) },
                        modifier = Modifier.weight(1f),
                        testTag = "filter_all"
                    )
                    CyberpunkChip(
                        text = "Videos",
                        selected = libraryFilter == LibraryFilter.VIDEOS,
                        onClick = { viewModel.setLibraryFilter(LibraryFilter.VIDEOS) },
                        modifier = Modifier.weight(1f),
                        testTag = "filter_videos"
                    )
                    CyberpunkChip(
                        text = "Audio Only",
                        selected = libraryFilter == LibraryFilter.AUDIO,
                        onClick = { viewModel.setLibraryFilter(LibraryFilter.AUDIO) },
                        modifier = Modifier.weight(1f),
                        testTag = "filter_audio"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Counter info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "OFFLINE VAULT",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
            Text(
                text = "${mediaList.size} item${if (mediaList.size != 1) "s" else ""}",
                color = NeonCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Content: List or Empty State
        if (mediaList.isEmpty()) {
            EmptyLibraryView(onNavigateDownloader = { viewModel.setTab(MainTab.DOWNLOADER) })
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("media_list"),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(mediaList, key = { it.id }) { item ->
                    MediaListItemCard(
                        item = item,
                        onPlay = { viewModel.playMediaInApp(item) },
                        onOptions = { viewModel.openMediaOptions(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun MediaListItemCard(
    item: MediaItemEntity,
    onPlay: () -> Unit,
    onOptions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val formattedDate = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        .format(Date(item.downloadTimestamp))

    CyberpunkGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onPlay)
            .testTag("media_item_${item.id}"),
        cornerRadius = 18.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Media Thumbnail / Icon
            Box(
                modifier = Modifier
                    .size(width = 86.dp, height = 64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(TranslucentCharcoalElevated),
                contentAlignment = Alignment.Center
            ) {
                if (item.thumbnailUrl.isNotBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(item.thumbnailUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = if (item.isAudioOnly) Icons.Default.MusicNote else Icons.Default.VideoLibrary,
                        contentDescription = null,
                        tint = if (item.isAudioOnly) ElectricPurple else NeonCyan,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Small duration tag
                if (item.durationSeconds > 0) {
                    val mins = item.durationSeconds / 60
                    val secs = item.durationSeconds % 60
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MidnightNavy.copy(alpha = 0.85f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = String.format("%d:%02d", mins, secs),
                            color = TextPrimary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Metadata info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Format Tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (item.isAudioOnly) ElectricPurple.copy(alpha = 0.2f) else NeonCyan.copy(alpha = 0.2f))
                            .border(1.dp, if (item.isAudioOnly) ElectricPurple.copy(alpha = 0.5f) else NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = item.format,
                            color = if (item.isAudioOnly) ElectricPurple else NeonCyan,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = if (item.isAudioOnly) item.bitrate else item.resolution,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "•", color = TextMuted, fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "${(item.fileSizeBytes / (1024 * 1024))} MB",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Saved $formattedDate",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            // Quick Play Button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(NeonCyan.copy(alpha = 0.15f))
                    .border(1.dp, NeonCyan.copy(alpha = 0.4f), CircleShape)
                    .clickable(onClick = onPlay),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = NeonCyan,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Options Button
            IconButton(
                onClick = onOptions,
                modifier = Modifier.testTag("more_options_${item.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyLibraryView(
    onNavigateDownloader: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(TranslucentCharcoalElevated)
                .border(1.5.dp, NeonCyan.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.GraphicEq,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Your Media Vault is Empty",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Downloaded media will be stored here for instant zero-buffering offline playback and synchronized with your device Gallery.",
            color = TextSecondary,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        CyberpunkGradientButton(
            text = "Go to Downloader",
            onClick = onNavigateDownloader,
            icon = Icons.Default.Download,
            modifier = Modifier.width(220.dp),
            testTag = "empty_go_downloader_btn"
        )
    }
}
