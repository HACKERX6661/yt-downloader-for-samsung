package com.example.data

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.OutputStream

object MediaStoreHelper {
    private const val TAG = "MediaStoreHelper"

    /**
     * Prepares a new MediaStore entry for writing.
     * Returns a Pair of the resulting content Uri and OutputStream, or null if creation failed.
     */
    fun createMediaStoreEntry(
        context: Context,
        fileName: String,
        mimeType: String,
        isAudio: Boolean
    ): Pair<Uri, OutputStream>? {
        val resolver = context.contentResolver
        val relativeSubfolder = if (isAudio) "Music/MediaVault" else "Movies/MediaVault"

        return try {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, relativeSubfolder)
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val collectionUri = if (isAudio) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } else {
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } else {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }
            }

            val uri = resolver.insert(collectionUri, contentValues) ?: return null
            val outputStream = resolver.openOutputStream(uri) ?: run {
                resolver.delete(uri, null, null)
                return null
            }

            Pair(uri, outputStream)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating MediaStore entry", e)
            null
        }
    }

    /**
     * Finishes writing to MediaStore by resetting IS_PENDING.
     */
    fun finishMediaStoreEntry(context: Context, uri: Uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.IS_PENDING, 0)
                }
                context.contentResolver.update(uri, values, null, null)
            } catch (e: Exception) {
                Log.e(TAG, "Error finishing MediaStore entry", e)
            }
        }
    }

    /**
     * Deletes an item by Uri from MediaStore or local files.
     */
    fun deleteMediaUri(context: Context, uriString: String): Boolean {
        return try {
            val uri = Uri.parse(uriString)
            if (uriString.startsWith("content://")) {
                val deleted = context.contentResolver.delete(uri, null, null)
                deleted > 0
            } else {
                val file = File(uri.path ?: uriString)
                if (file.exists()) file.delete() else true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting media: $uriString", e)
            false
        }
    }

    /**
     * Opens the media in the native Gallery or system video player.
     */
    fun openInGallery(context: Context, uriString: String, isAudio: Boolean) {
        try {
            val uri = Uri.parse(uriString)
            val mimeType = if (isAudio) "audio/*" else "video/*"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Open in Gallery / Player").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open in Gallery", e)
            Toast.makeText(context, "No gallery or media player found to open this file", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Shares the media file with other apps.
     */
    fun shareMedia(context: Context, uriString: String, title: String, isAudio: Boolean) {
        try {
            val uri = Uri.parse(uriString)
            val mimeType = if (isAudio) "audio/*" else "video/*"
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, "Shared from MediaVault: $title")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Share media via").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            Log.e(TAG, "Failed to share media", e)
            Toast.makeText(context, "Could not share file", Toast.LENGTH_SHORT).show()
        }
    }
}
