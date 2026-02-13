package com.app.ai.mclint.feature_filemanager.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ai.mclint.feature_filemanager.domain.model.FileItem
import com.app.ai.mclint.feature_filemanager.domain.model.FileOperationResult
import com.app.ai.mclint.feature_filemanager.domain.model.SelectionState
import com.app.ai.mclint.feature_filemanager.domain.model.SortOption
import com.app.ai.mclint.feature_filemanager.domain.model.ViewMode
import com.app.ai.mclint.feature_filemanager.domain.repository.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for File Manager
 */
data class FileManagerUiState(
    val currentPath: String = "/",
    val files: List<FileItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val sortOption: SortOption = SortOption.NAME,
    val viewMode: ViewMode = ViewMode.LIST,
    val showHidden: Boolean = false,
    val searchQuery: String = "",
    val isSearchMode: Boolean = false,
    val searchResults: List<FileItem> = emptyList(),
    val selectionState: SelectionState = SelectionState(),
    val pathHistory: List<String> = emptyList()
)

/**
 * ViewModel for File Manager screen
 */
@HiltViewModel
class FileManagerViewModel @Inject constructor(
    private val fileRepository: FileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FileManagerUiState())
    val uiState: StateFlow<FileManagerUiState> = _uiState.asStateFlow()

    init {
        loadRootDirectory()
    }

    /**
     * Load the root storage directory
     */
    fun loadRootDirectory() {
        val rootPath = fileRepository.getRootDirectory()
        navigateToDirectory(rootPath)
    }

    /**
     * Navigate to a specific directory
     */
    fun navigateToDirectory(path: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = fileRepository.listFiles(path, _uiState.value.showHidden)) {
                is FileOperationResult.Success -> {
                    val sortedFiles = sortFiles(result.data, _uiState.value.sortOption)
                    _uiState.update { state ->
                        state.copy(
                            currentPath = path,
                            files = sortedFiles,
                            isLoading = false,
                            isSearchMode = false,
                            searchQuery = "",
                            searchResults = emptyList(),
                            pathHistory = state.pathHistory + path
                        )
                    }
                }
                is FileOperationResult.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = result.message
                    )}
                }
            }
        }
    }

    /**
     * Navigate back to parent directory
     */
    fun navigateBack(): Boolean {
        val currentPath = _uiState.value.currentPath
        val parentPath = java.io.File(currentPath).parent
        
        return if (parentPath != null && parentPath != currentPath) {
            navigateToDirectory(parentPath)
            true
        } else {
            false
        }
    }

    /**
     * Create a new file
     */
    fun createFile(name: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val path = "${_uiState.value.currentPath}/$name"
            
            when (val result = fileRepository.createFile(path)) {
                is FileOperationResult.Success -> {
                    refreshCurrentDirectory()
                    onResult(true, "File created successfully")
                }
                is FileOperationResult.Error -> {
                    onResult(false, result.message)
                }
            }
        }
    }

    /**
     * Create a new directory
     */
    fun createDirectory(name: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val path = "${_uiState.value.currentPath}/$name"
            
            when (val result = fileRepository.createDirectory(path)) {
                is FileOperationResult.Success -> {
                    refreshCurrentDirectory()
                    onResult(true, "Directory created successfully")
                }
                is FileOperationResult.Error -> {
                    onResult(false, result.message)
                }
            }
        }
    }

    /**
     * Delete a file or directory
     */
    fun delete(file: FileItem, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            when (val result = fileRepository.delete(file.path)) {
                is FileOperationResult.Success -> {
                    refreshCurrentDirectory()
                    onResult(true, "Deleted successfully")
                }
                is FileOperationResult.Error -> {
                    onResult(false, result.message)
                }
            }
        }
    }

    /**
     * Delete selected files
     */
    fun deleteSelected(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val selectedFiles = _uiState.value.selectionState.selectedFiles
            var successCount = 0
            var failCount = 0
            
            selectedFiles.forEach { file ->
                when (fileRepository.delete(file.path)) {
                    is FileOperationResult.Success -> successCount++
                    is FileOperationResult.Error -> failCount++
                }
            }
            
            clearSelection()
            refreshCurrentDirectory()
            
            val message = if (failCount == 0) {
                "Deleted $successCount items"
            } else {
                "Deleted $successCount items, failed $failCount"
            }
            onResult(failCount == 0, message)
        }
    }

    /**
     * Rename a file or directory
     */
    fun rename(file: FileItem, newName: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            when (val result = fileRepository.rename(file.path, newName)) {
                is FileOperationResult.Success -> {
                    refreshCurrentDirectory()
                    onResult(true, "Renamed successfully")
                }
                is FileOperationResult.Error -> {
                    onResult(false, result.message)
                }
            }
        }
    }

    /**
     * Copy a file
     */
    fun copy(file: FileItem, destinationPath: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val newFileName = file.name
            val destination = "$destinationPath/$newFileName"
            
            when (val result = fileRepository.copy(file.path, destination)) {
                is FileOperationResult.Success -> {
                    onResult(true, "Copied successfully")
                }
                is FileOperationResult.Error -> {
                    onResult(false, result.message)
                }
            }
        }
    }

    /**
     * Move a file
     */
    fun move(file: FileItem, destinationPath: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val newFileName = file.name
            val destination = "$destinationPath/$newFileName"
            
            when (val result = fileRepository.move(file.path, destination)) {
                is FileOperationResult.Success -> {
                    refreshCurrentDirectory()
                    onResult(true, "Moved successfully")
                }
                is FileOperationResult.Error -> {
                    onResult(false, result.message)
                }
            }
        }
    }

    /**
     * Search files
     */
    fun searchFiles(query: String) {
        if (query.isBlank()) {
            _uiState.update { it.copy(
                isSearchMode = false,
                searchQuery = "",
                searchResults = emptyList()
            )}
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, searchQuery = query) }
            
            when (val result = fileRepository.searchFiles(query, _uiState.value.currentPath, true)) {
                is FileOperationResult.Success -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        isSearchMode = true,
                        searchResults = result.data
                    )}
                }
                is FileOperationResult.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = result.message
                    )}
                }
            }
        }
    }

    /**
     * Clear search and return to normal mode
     */
    fun clearSearch() {
        _uiState.update { it.copy(
            isSearchMode = false,
            searchQuery = "",
            searchResults = emptyList()
        )}
    }

    /**
     * Toggle selection mode
     */
    fun toggleSelectionMode() {
        _uiState.update { state ->
            state.copy(
                selectionState = if (state.selectionState.isSelectionMode) {
                    SelectionState()
                } else {
                    SelectionState(isSelectionMode = true)
                }
            )
        }
    }

    /**
     * Toggle file selection
     */
    fun toggleFileSelection(file: FileItem) {
        _uiState.update { state ->
            val currentSelected = state.selectionState.selectedFiles
            val newSelected = if (currentSelected.contains(file)) {
                currentSelected - file
            } else {
                currentSelected + file
            }
            
            state.copy(
                selectionState = state.selectionState.copy(
                    selectedFiles = newSelected,
                    isSelectionMode = newSelected.isNotEmpty()
                )
            )
        }
    }

    /**
     * Select all files
     */
    fun selectAll() {
        _uiState.update { state ->
            state.copy(
                selectionState = state.selectionState.copy(
                    selectedFiles = state.files.toSet(),
                    isSelectionMode = true
                )
            )
        }
    }

    /**
     * Clear selection
     */
    fun clearSelection() {
        _uiState.update { state ->
            state.copy(selectionState = SelectionState())
        }
    }

    /**
     * Set sort option
     */
    fun setSortOption(sortOption: SortOption) {
        _uiState.update { state ->
            val sortedFiles = sortFiles(state.files, sortOption)
            state.copy(
                sortOption = sortOption,
                files = sortedFiles
            )
        }
    }

    /**
     * Set view mode
     */
    fun setViewMode(viewMode: ViewMode) {
        _uiState.update { it.copy(viewMode = viewMode) }
    }

    /**
     * Toggle show hidden files
     */
    fun toggleShowHidden() {
        _uiState.update { state ->
            state.copy(showHidden = !state.showHidden)
        }
        refreshCurrentDirectory()
    }

    /**
     * Refresh current directory
     */
    fun refreshCurrentDirectory() {
        navigateToDirectory(_uiState.value.currentPath)
    }

    /**
     * Clear error
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Sort files based on sort option
     */
    private fun sortFiles(files: List<FileItem>, sortOption: SortOption): List<FileItem> {
        return when (sortOption) {
            SortOption.NAME -> files.sortedWith(
                compareBy<FileItem> { !it.isDirectory }
                    .thenBy { it.name.lowercase() }
            )
            SortOption.DATE -> files.sortedWith(
                compareBy<FileItem> { !it.isDirectory }
                    .thenByDescending { it.lastModified }
            )
            SortOption.SIZE -> files.sortedWith(
                compareBy<FileItem> { !it.isDirectory }
                    .thenByDescending { it.size }
            )
            SortOption.TYPE -> files.sortedWith(
                compareBy<FileItem> { !it.isDirectory }
                    .thenBy { it.extension }
                    .thenBy { it.name.lowercase() }
            )
        }
    }
}
