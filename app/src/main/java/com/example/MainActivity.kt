package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainTab
import com.example.ui.MediaVaultViewModel
import com.example.ui.components.LegalDisclaimerModal
import com.example.ui.components.StoragePermissionDialog
import com.example.ui.downloader.DownloaderScreen
import com.example.ui.library.LibraryScreen
import com.example.ui.library.MediaOptionsSheet
import com.example.ui.player.ExoPlayerScreen
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.GlowingEmerald
import com.example.ui.theme.MediaVaultTheme
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TranslucentCharcoal
import com.example.ui.theme.TranslucentCharcoalElevated

class MainActivity : ComponentActivity() {

    private val viewModel: MediaVaultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MediaVaultTheme {
                val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
                val activePlayingMedia by viewModel.activePlayingMedia.collectAsStateWithLifecycle()
                val showPermissionDialog by viewModel.showPermissionDialog.collectAsStateWithLifecycle()
                val showLegalDisclaimer by viewModel.showLegalDisclaimer.collectAsStateWithLifecycle()
                val optionsMediaItem by viewModel.optionsMediaItem.collectAsStateWithLifecycle()
                val libraryMedia by viewModel.libraryMedia.collectAsStateWithLifecycle()

                // Permission Launcher
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) {
                    viewModel.onPermissionGrantedAndStartDownload()
                }

                // If currently playing a media file, show the ExoPlayer fullscreen view
                if (activePlayingMedia != null) {
                    ExoPlayerScreen(
                        mediaItem = activePlayingMedia!!,
                        onClose = { viewModel.closePlayer() }
                    )
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = MidnightNavy,
                        contentWindowInsets = WindowInsets.safeDrawing,
                        bottomBar = {
                            CyberpunkBottomNavigationBar(
                                currentTab = currentTab,
                                onTabSelected = { viewModel.setTab(it) },
                                libraryCount = libraryMedia.size,
                                modifier = Modifier.navigationBarsPadding()
                            )
                        }
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            AnimatedContent(
                                targetState = currentTab,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "tabTransition",
                                modifier = Modifier.weight(1f)
                            ) { tab ->
                                when (tab) {
                                    MainTab.DOWNLOADER -> DownloaderScreen(viewModel = viewModel)
                                    MainTab.LIBRARY -> LibraryScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }
                }

                // Compliance Dialogs
                if (showPermissionDialog) {
                    StoragePermissionDialog(
                        onConfirm = {
                            val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                arrayOf(
                                    Manifest.permission.READ_MEDIA_VIDEO,
                                    Manifest.permission.READ_MEDIA_AUDIO
                                )
                            } else {
                                arrayOf(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )
                            }
                            permissionLauncher.launch(perms)
                        },
                        onDismiss = { viewModel.dismissPermissionDialog() }
                    )
                }

                if (showLegalDisclaimer) {
                    LegalDisclaimerModal(
                        onAccept = { viewModel.acceptLegalDisclaimer() },
                        onDismiss = { viewModel.dismissLegalDisclaimer() }
                    )
                }

                // More Options Bottom Sheet
                optionsMediaItem?.let { item ->
                    MediaOptionsSheet(
                        item = item,
                        onDismiss = { viewModel.closeMediaOptions() },
                        onPlayInApp = { viewModel.playMediaInApp(item) },
                        onOpenInGallery = { viewModel.openInGallery(item) },
                        onShare = { viewModel.shareMedia(item) },
                        onDelete = { viewModel.deleteMedia(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun CyberpunkBottomNavigationBar(
    currentTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    libraryCount: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(TranslucentCharcoal)
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        listOf(
                            NeonCyan.copy(alpha = 0.4f),
                            Color(0x22FFFFFF),
                            ElectricPurple.copy(alpha = 0.4f)
                        )
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavBarTabItem(
                    title = "Downloader",
                    icon = Icons.Default.Download,
                    isSelected = currentTab == MainTab.DOWNLOADER,
                    testTag = "nav_downloader",
                    onClick = { onTabSelected(MainTab.DOWNLOADER) }
                )

                NavBarTabItem(
                    title = "Vault Library",
                    icon = Icons.Default.VideoLibrary,
                    isSelected = currentTab == MainTab.LIBRARY,
                    badgeCount = libraryCount,
                    testTag = "nav_library",
                    onClick = { onTabSelected(MainTab.LIBRARY) }
                )
            }
        }
    }
}

@Composable
fun NavBarTabItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    badgeCount: Int = 0
) {
    val activeBg = if (isSelected) NeonCyan.copy(alpha = 0.16f) else Color.Transparent
    val activeBorder = if (isSelected) NeonCyan.copy(alpha = 0.6f) else Color.Transparent
    val iconTint = if (isSelected) NeonCyan else TextMuted
    val textColor = if (isSelected) TextPrimary else TextMuted

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(activeBg)
            .border(1.dp, activeBorder, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )

        if (badgeCount > 0) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isSelected) NeonCyan else Color(0x33FFFFFF))
                    .padding(horizontal = 6.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "$badgeCount",
                    color = if (isSelected) MidnightNavy else TextPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
