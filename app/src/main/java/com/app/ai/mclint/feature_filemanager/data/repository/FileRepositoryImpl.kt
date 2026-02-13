package com.app.ai.mclint.feature_filemanager.data.repository

import android.content.Context
import android.os.Environment
import android.webkit.MimeTypeMap
import com.app.ai.mclint.feature_filemanager.domain.model.FileItem
import com.app.ai.mclint.feature_filemanager.domain.model.FileOperationResult
import com.app.ai.mclint.feature_filemanager.domain.repository.FileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of FileRepository using Java File API
 */
@Singleton
class FileRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : FileRepository {

    override suspend fun listFiles(
        path: String,
        showHidden: Boolean
    ): FileOperationResult<List<FileItem>> = withContext(Dispatchers.IO) {
        try {
            val directory = File(path)
            
            if (!directory.exists()) {
                return@withContext FileOperationResult.Error("Directory does not exist")
            }
            
            if (!directory.isDirectory) {
                return@withContext FileOperationResult.Error("Path is not a directory")
            }
            
            if (!directory.canRead()) {
                return@withContext FileOperationResult.Error("Cannot read directory")
            }
            
            val files = directory.listFiles()
                ?.filter { showHidden || !it.name.startsWith(".") }
                ?.map { it.toFileItem() }
                ?.sortedWith(compareBy<FileItem> { !it.isDirectory }.thenBy { it.name.lowercase() })
                ?: emptyList()
            
            FileOperationResult.Success(files)
        } catch (e: Exception) {
            FileOperationResult.Error("Failed to list files: ${e.message}", e)
        }
    }

    override suspend fun createFile(path: String): FileOperationResult<FileItem> = withContext(Dispatchers.IO) {
        try {
            val file = File(path)
            
            if (file.exists()) {
                return@withContext FileOperationResult.Error("File already exists")
            }
            
            val created = file.createNewFile()
            if (created) {
                FileOperationResult.Success(file.toFileItem())
            } else {
                FileOperationResult.Error("Failed to create file")
            }
        } catch (e: Exception) {
            FileOperationResult.Error("Failed to create file: ${e.message}", e)
        }
    }

    override suspend fun createDirectory(path: String): FileOperationResult<FileItem> = withContext(Dispatchers.IO) {
        try {
            val directory = File(path)
            
            if (directory.exists()) {
                return@withContext FileOperationResult.Error("Directory already exists")
            }
            
            val created = directory.mkdirs()
            if (created) {
                FileOperationResult.Success(directory.toFileItem())
            } else {
                FileOperationResult.Error("Failed to create directory")
            }
        } catch (e: Exception) {
            FileOperationResult.Error("Failed to create directory: ${e.message}", e)
        }
    }

    override suspend fun delete(path: String): FileOperationResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val file = File(path)
            
            if (!file.exists()) {
                return@withContext FileOperationResult.Error("File does not exist")
            }
            
            val deleted = if (file.isDirectory) {
                file.deleteRecursively()
            } else {
                file.delete()
            }
            
            if (deleted) {
                FileOperationResult.Success(Unit)
            } else {
                FileOperationResult.Error("Failed to delete")
            }
        } catch (e: Exception) {
            FileOperationResult.Error("Failed to delete: ${e.message}", e)
        }
    }

    override suspend fun copy(
        sourcePath: String,
        destinationPath: String
    ): FileOperationResult<FileItem> = withContext(Dispatchers.IO) {
        try {
            val source = File(sourcePath)
            val destination = File(destinationPath)
            
            if (!source.exists()) {
                return@withContext FileOperationResult.Error("Source does not exist")
            }
            
            if (destination.exists()) {
                return@withContext FileOperationResult.Error("Destination already exists")
            }
            
            val copied = if (source.isDirectory) {
                source.copyRecursively(destination, overwrite = false)
            } else {
                source.copyTo(destination, overwrite = false)
                true
            }
            
            if (copied) {
                FileOperationResult.Success(destination.toFileItem())
            } else {
                FileOperationResult.Error("Failed to copy")
            }
        } catch (e: Exception) {
            FileOperationResult.Error("Failed to copy: ${e.message}", e)
        }
    }

    override suspend fun move(
        sourcePath: String,
        destinationPath: String
    ): FileOperationResult<FileItem> = withContext(Dispatchers.IO) {
        try {
            val source = File(sourcePath)
            val destination = File(destinationPath)
            
            if (!source.exists()) {
                return@withContext FileOperationResult.Error("Source does not exist")
            }
            
            if (destination.exists()) {
                return@withContext FileOperationResult.Error("Destination already exists")
            }
            
            val moved = source.renameTo(destination)
            
            if (moved) {
                FileOperationResult.Success(destination.toFileItem())
            } else {
                FileOperationResult.Error("Failed to move")
            }
        } catch (e: Exception) {
            FileOperationResult.Error("Failed to move: ${e.message}", e)
        }
    }

    override suspend fun rename(
        path: String,
        newName: String
    ): FileOperationResult<FileItem> = withContext(Dispatchers.IO) {
        try {
            val file = File(path)
            
            if (!file.exists()) {
                return@withContext FileOperationResult.Error("File does not exist")
            }
            
            val renamed = file.renameTo(File(file.parentFile, newName))
            
            if (renamed) {
                FileOperationResult.Success(File(file.parentFile, newName).toFileItem())
            } else {
                FileOperationResult.Error("Failed to rename")
            }
        } catch (e: Exception) {
            FileOperationResult.Error("Failed to rename: ${e.message}", e)
        }
    }

    override suspend fun readText(path: String): FileOperationResult<String> = withContext(Dispatchers.IO) {
        try {
            val file = File(path)
            
            if (!file.exists()) {
                return@withContext FileOperationResult.Error("File does not exist")
            }
            
            if (!file.canRead()) {
                return@withContext FileOperationResult.Error("Cannot read file")
            }
            
            val content = file.readText()
            FileOperationResult.Success(content)
        } catch (e: Exception) {
            FileOperationResult.Error("Failed to read file: ${e.message}", e)
        }
    }

    override suspend fun writeText(
        path: String,
        content: String,
        append: Boolean
    ): FileOperationResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val file = File(path)
            
            if (!file.exists()) {
                file.createNewFile()
            }
            
            if (!file.canWrite()) {
                return@withContext FileOperationResult.Error("Cannot write to file")
            }
            
            if (append) {
                file.appendText(content)
            } else {
                file.writeText(content)
            }
            
            FileOperationResult.Success(Unit)
        } catch (e: Exception) {
            FileOperationResult.Error("Failed to write file: ${e.message}", e)
        }
    }

    override suspend fun getInputStream(path: String): FileOperationResult<InputStream> = withContext(Dispatchers.IO) {
        try {
            val file = File(path)
            
            if (!file.exists()) {
                return@withContext FileOperationResult.Error("File does not exist")
            }
            
            FileOperationResult.Success(FileInputStream(file))
        } catch (e: Exception) {
            FileOperationResult.Error("Failed to get input stream: ${e.message}", e)
        }
    }

    override suspend fun searchFiles(
        query: String,
        basePath: String,
        recursive: Boolean
    ): FileOperationResult<List<FileItem>> = withContext(Dispatchers.IO) {
        try {
            val baseDir = File(basePath)
            
            if (!baseDir.exists() || !baseDir.isDirectory) {
                return@withContext FileOperationResult.Error("Invalid base directory")
            }
            
            val results = mutableListOf<FileItem>()
            val lowerQuery = query.lowercase()
            
            fun searchInDirectory(directory: File) {
                directory.listFiles()?.forEach { file ->
                    if (file.name.lowercase().contains(lowerQuery)) {
                        results.add(file.toFileItem())
                    }
                    if (recursive && file.isDirectory && file.canRead()) {
                        searchInDirectory(file)
                    }
                }
            }
            
            searchInDirectory(baseDir)
            FileOperationResult.Success(results)
        } catch (e: Exception) {
            FileOperationResult.Error("Failed to search files: ${e.message}", e)
        }
    }

    override suspend fun exists(path: String): Boolean = withContext(Dispatchers.IO) {
        File(path).exists()
    }

    override suspend fun getFileInfo(path: String): FileOperationResult<FileItem> = withContext(Dispatchers.IO) {
        try {
            val file = File(path)
            
            if (!file.exists()) {
                return@withContext FileOperationResult.Error("File does not exist")
            }
            
            FileOperationResult.Success(file.toFileItem())
        } catch (e: Exception) {
            FileOperationResult.Error("Failed to get file info: ${e.message}", e)
        }
    }

    override fun getRootDirectory(): String {
        return Environment.getExternalStorageDirectory().absolutePath
    }

    override fun getStorageDirectories(): List<String> {
        val directories = mutableListOf<String>()
        
        // Primary external storage
        Environment.getExternalStorageDirectory().absolutePath.let {
            directories.add(it)
        }
        
        // Secondary storage (if available)
        System.getenv("SECONDARY_STORAGE")?.let {
            directories.add(it)
        }
        
        return directories
    }

    /**
     * Extension function to convert File to FileItem
     */
    private fun File.toFileItem(): FileItem {
        return FileItem(
            id = absolutePath,
            name = name,
            path = absolutePath,
            isDirectory = isDirectory,
            size = if (isDirectory) 0L else length(),
            lastModified = lastModified(),
            mimeType = if (isDirectory) null else getMimeType(extension),
            extension = if (isDirectory) null else extension,
            isHidden = name.startsWith("."),
            canRead = canRead(),
            canWrite = canWrite()
        )
    }

    /**
     * Get MIME type from file extension
     */
    private fun getMimeType(extension: String): String? {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
    }
}
