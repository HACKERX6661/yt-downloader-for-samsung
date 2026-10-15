package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.downloader.DownloadStatus
import com.example.downloader.DownloadTaskProgress
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.GlowingEmerald
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TranslucentCharcoalElevated

@Composable
fun RealTimeDownloadCard(
    task: DownloadTaskProgress,
    onDismiss: () -> Unit,
    onPlay: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = task.progressPercent,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "downloadProgress"
    )

    val (badgeText, badgeColor) = when (task.status) {
        DownloadStatus.CONNECTING -> Pair("CONNECTING", NeonCyan)
        DownloadStatus.DOWNLOADING -> Pair("DOWNLOADING", ElectricPurple)
        DownloadStatus.MERGING -> Pair("MERGING", NeonAmber)
        DownloadStatus.SAVED_TO_GALLERY -> Pair("GALLERY SYNC", GlowingEmerald)
        DownloadStatus.COMPLETED -> Pair("SAVED", GlowingEmerald)
        DownloadStatus.ERROR -> Pair("ERROR", NeonPink)
        DownloadStatus.IDLE -> Pair("IDLE", TextMuted)
    }

    CyberpunkGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("real_time_download_card"),
        cornerRadius = 20.dp,
        borderGlowColor = badgeColor.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Row: Title, Badge, and Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(badgeColor.copy(alpha = 0.15f))
                        .border(1.dp, badgeColor.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (task.status) {
                            DownloadStatus.COMPLETED, DownloadStatus.SAVED_TO_GALLERY -> Icons.Default.CheckCircle
                            DownloadStatus.MERGING -> Icons.Default.Sync
                            DownloadStatus.ERROR -> Icons.Default.ErrorOutline
                            else -> Icons.Default.CloudDownload
                        },
                        contentDescription = null,
                        tint = badgeColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = task.statusMessage,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .border(1.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = badgeColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                if (task.status == DownloadStatus.COMPLETED || task.status == DownloadStatus.ERROR) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("dismiss_download_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Bar Track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(TranslucentCharcoalElevated)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(NeonCyan, ElectricPurple, GlowingEmerald)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Telemetry: Speed, ETA, Percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (task.status == DownloadStatus.DOWNLOADING && task.downloadSpeedMBs > 0) {
                        Text(
                            text = String.format("%.1f MB/s", task.downloadSpeedMBs),
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "•",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${task.etaSeconds}s ETA",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    } else if (task.status == DownloadStatus.MERGING) {
                        Text(
                            text = "yt-dlp + FFmpeg stream muxing",
                            color = NeonAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    } else if (task.status == DownloadStatus.SAVED_TO_GALLERY || task.status == DownloadStatus.COMPLETED) {
                        Text(
                            text = "Indexed in Gallery (Movies/MediaVault)",
                            color = GlowingEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    color = if (task.status == DownloadStatus.COMPLETED) GlowingEmerald else TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Quick Play Button if completed
            AnimatedVisibility(visible = task.status == DownloadStatus.COMPLETED && task.savedUri != null) {
                Column {
                    Spacer(modifier = Modifier.height(14.dp))
                    CyberpunkGradientButton(
                        text = "Play Now in MediaVault",
                        onClick = { task.savedUri?.let { onPlay?.invoke(it) } },
                        icon = Icons.Default.PlayArrow,
                        modifier = Modifier.height(44.dp),
                        isEmerald = true,
                        testTag = "play_now_btn"
                    )
                }
            }
        }
    }
}
