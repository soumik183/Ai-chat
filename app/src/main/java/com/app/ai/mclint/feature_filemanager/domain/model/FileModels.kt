package com.app.ai.mclint.feature_filemanager.domain.model

import com.app.ai.mclint.core.util.Constants

/**
 * Represents a file or directory in the file system
 */
data class FileItem(
    val id: String,
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long,
    val lastModified: Long,
    val mimeType: String? = null,
    val extension: String? = null,
    val isHidden: Boolean = false,
    val canRead: Boolean = true,
    val canWrite: Boolean = true
) {
    /**
     * Get the file type based on extension
     */
    fun getFileType(): String {
        if (isDirectory) return Constants.FILE_TYPE_FOLDER
        
        val ext = extension?.lowercase() ?: return Constants.FILE_TYPE_OTHER
        
        return when {
            Constants.CODE_EXTENSIONS.contains(ext) -> Constants.FILE_TYPE_CODE
            Constants.IMAGE_EXTENSIONS.contains(ext) -> Constants.FILE_TYPE_IMAGE
            Constants.VIDEO_EXTENSIONS.contains(ext) -> Constants.FILE_TYPE_VIDEO
            Constants.AUDIO_EXTENSIONS.contains(ext) -> Constants.FILE_TYPE_AUDIO
            Constants.ARCHIVE_EXTENSIONS.contains(ext) -> Constants.FILE_TYPE_ARCHIVE
            Constants.DOCUMENT_EXTENSIONS.contains(ext) -> Constants.FILE_TYPE_DOCUMENT
            else -> Constants.FILE_TYPE_OTHER
        }
    }
    
    /**
     * Check if this is a code file
     */
    fun isCodeFile(): Boolean {
        return getFileType() == Constants.FILE_TYPE_CODE
    }
    
    /**
     * Get formatted file size
     */
    fun getFormattedSize(): String {
        if (isDirectory) return ""
        
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> String.format("%.1f KB", size / 1024.0)
            size < 1024 * 1024 * 1024 -> String.format("%.1f MB", size / (1024.0 * 1024))
            else -> String.format("%.1f GB", size / (1024.0 * 1024 * 1024))
        }
    }
}

/**
 * Sort options for file listing
 */
enum class SortOption {
    NAME,
    DATE,
    SIZE,
    TYPE
}

/**
 * View mode for file display
 */
enum class ViewMode {
    LIST,
    GRID
}

/**
 * File operation result
 */
sealed class FileOperationResult<out T> {
    data class Success<T>(val data: T) : FileOperationResult<T>()
    data class Error(val message: String, val exception: Throwable? = null) : FileOperationResult<Nothing>()
}

/**
 * Batch operation type
 */
enum class BatchOperation {
    COPY,
    MOVE,
    DELETE
}

/**
 * Selected files state for batch operations
 */
data class SelectionState(
    val selectedFiles: Set<FileItem> = emptySet(),
    val isSelectionMode: Boolean = false
)
