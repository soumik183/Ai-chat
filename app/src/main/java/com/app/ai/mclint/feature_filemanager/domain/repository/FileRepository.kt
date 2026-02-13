package com.app.ai.mclint.feature_filemanager.domain.repository

import com.app.ai.mclint.feature_filemanager.domain.model.FileItem
import com.app.ai.mclint.feature_filemanager.domain.model.FileOperationResult
import java.io.InputStream

/**
 * Repository interface for file operations
 */
interface FileRepository {
    
    /**
     * List files in a directory
     * @param path Directory path
     * @param showHidden Whether to show hidden files
     * @return List of files or error
     */
    suspend fun listFiles(
        path: String,
        showHidden: Boolean = false
    ): FileOperationResult<List<FileItem>>
    
    /**
     * Create a new file
     * @param path Full path for the new file
     * @return Success or error
     */
    suspend fun createFile(path: String): FileOperationResult<FileItem>
    
    /**
     * Create a new directory
     * @param path Full path for the new directory
     * @return Success or error
     */
    suspend fun createDirectory(path: String): FileOperationResult<FileItem>
    
    /**
     * Delete a file or directory
     * @param path Path to delete
     * @return Success or error
     */
    suspend fun delete(path: String): FileOperationResult<Unit>
    
    /**
     * Copy a file or directory
     * @param sourcePath Source path
     * @param destinationPath Destination path
     * @return Success with new file item or error
     */
    suspend fun copy(
        sourcePath: String,
        destinationPath: String
    ): FileOperationResult<FileItem>
    
    /**
     * Move a file or directory
     * @param sourcePath Source path
     * @param destinationPath Destination path
     * @return Success with new file item or error
     */
    suspend fun move(
        sourcePath: String,
        destinationPath: String
    ): FileOperationResult<FileItem>
    
    /**
     * Rename a file or directory
     * @param path Current path
     * @param newName New name
     * @return Success with new file item or error
     */
    suspend fun rename(
        path: String,
        newName: String
    ): FileOperationResult<FileItem>
    
    /**
     * Read file content as text
     * @param path File path
     * @return Success with content or error
     */
    suspend fun readText(path: String): FileOperationResult<String>
    
    /**
     * Write text to a file
     * @param path File path
     * @param content Content to write
     * @param append Whether to append to existing content
     * @return Success or error
     */
    suspend fun writeText(
        path: String,
        content: String,
        append: Boolean = false
    ): FileOperationResult<Unit>
    
    /**
     * Get file input stream
     * @param path File path
     * @return Success with input stream or error
     */
    suspend fun getInputStream(path: String): FileOperationResult<InputStream>
    
    /**
     * Search files by name pattern
     * @param query Search query
     * @param basePath Base directory to search in
     * @param recursive Whether to search recursively
     * @return List of matching files or error
     */
    suspend fun searchFiles(
        query: String,
        basePath: String,
        recursive: Boolean = true
    ): FileOperationResult<List<FileItem>>
    
    /**
     * Check if a file exists
     * @param path File path
     * @return True if exists, false otherwise
     */
    suspend fun exists(path: String): Boolean
    
    /**
     * Get file info
     * @param path File path
     * @return File item or error
     */
    suspend fun getFileInfo(path: String): FileOperationResult<FileItem>
    
    /**
     * Get the root storage directory
     * @return Root directory path
     */
    fun getRootDirectory(): String
    
    /**
     * Get available storage directories
     * @return List of storage directory paths
     */
    fun getStorageDirectories(): List<String>
}
