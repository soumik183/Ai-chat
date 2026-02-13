package com.app.ai.mclint.core.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Enum representing the state of storage permission
 */
enum class StoragePermissionState {
    GRANTED,
    DENIED,
    MANAGE_APP_FILES,  // Android 11+ requires MANAGE_EXTERNAL_STORAGE
    LEGACY_STORAGE     // Android 10 and below
}

/**
 * Manager class for handling storage permissions
 */
class PermissionManager(private val context: Context) {

    /**
     * Check the current state of storage permission
     */
    fun checkStoragePermission(): StoragePermissionState {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                // Android 11+ (API 30+)
                if (Environment.isExternalStorageManager()) {
                    StoragePermissionState.GRANTED
                } else {
                    StoragePermissionState.MANAGE_APP_FILES
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                // Android 10 (API 29) - Scoped storage
                val readPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                
                if (readPermission) {
                    StoragePermissionState.GRANTED
                } else {
                    StoragePermissionState.LEGACY_STORAGE
                }
            }
            else -> {
                // Android 9 and below
                val readPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                
                val writePermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                
                if (readPermission && writePermission) {
                    StoragePermissionState.GRANTED
                } else {
                    StoragePermissionState.LEGACY_STORAGE
                }
            }
        }
    }

    /**
     * Check if storage permission is granted
     */
    fun hasStoragePermission(): Boolean {
        return checkStoragePermission() == StoragePermissionState.GRANTED
    }

    /**
     * Request storage permission based on Android version
     */
    fun requestStoragePermission(activity: Activity, requestCode: Int) {
        when (checkStoragePermission()) {
            StoragePermissionState.MANAGE_APP_FILES -> {
                // Android 11+ - Open settings for MANAGE_EXTERNAL_STORAGE
                openManageAppFilesSettings(activity)
            }
            StoragePermissionState.LEGACY_STORAGE -> {
                // Android 10 and below - Request runtime permissions
                val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_AUDIO
                    )
                } else {
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                }
                ActivityCompat.requestPermissions(activity, permissions, requestCode)
            }
            else -> { /* Already granted */ }
        }
    }

    /**
     * Open app settings for MANAGE_EXTERNAL_STORAGE permission
     */
    fun openManageAppFilesSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
            data = Uri.parse("package:${context.packageName}")
        }
        activity.startActivity(intent)
    }

    /**
     * Open app settings page
     */
    fun openAppSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${context.packageName}")
        }
        activity.startActivity(intent)
    }

    /**
     * Check if should show permission rationale
     */
    fun shouldShowRequestPermissionRationale(activity: Activity): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        } else {
            false
        }
    }

    /**
     * Get required permissions for current Android version
     */
    fun getRequiredPermissions(): Array<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO
                )
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                arrayOf() // MANAGE_EXTERNAL_STORAGE is not a runtime permission
            }
            else -> {
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }
    }
}
