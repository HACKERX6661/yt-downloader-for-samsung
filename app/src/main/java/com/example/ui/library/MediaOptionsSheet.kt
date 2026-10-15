package com.example.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MediaItemEntity
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.GlowingEmerald
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TranslucentCharcoal
import com.example.ui.theme.TranslucentCharcoalElevated

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaOptionsSheet(
    item: MediaItemEntity,
    onDismiss: () -> Unit,
    onPlayInApp: () -> Unit,
    onOpenInGallery: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TranslucentCharcoal,
        contentColor = TextPrimary,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .testTag("media_options_sheet")
        ) {
            // Header Info
            Text(
                text = item.title,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${item.format} • ${item.resolution} • ${item.bitrate} • ${(item.fileSizeBytes / (1024 * 1024))} MB",
                color = TextMuted,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Options List
            OptionRowItem(
                icon = Icons.Default.PlayCircleFilled,
                label = "Play in App (ExoPlayer)",
                color = NeonCyan,
                testTag = "opt_play_app",
                onClick = onPlayInApp
            )

            OptionRowItem(
                icon = Icons.Default.OpenInNew,
                label = "Open in Gallery / External Player",
                color = ElectricPurple,
                testTag = "opt_open_gallery",
                onClick = onOpenInGallery
            )

            OptionRowItem(
                icon = Icons.Default.Share,
                label = "Share Media File",
                color = GlowingEmerald,
                testTag = "opt_share_media",
                onClick = onShare
            )

            OptionRowItem(
                icon = Icons.Default.Delete,
                label = "Delete from Device & Library",
                color = NeonPink,
                testTag = "opt_delete_media",
                onClick = onDelete
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun OptionRowItem(
    icon: ImageVector,
    label: String,
    color: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            color = if (color == NeonPink) NeonPink else TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
